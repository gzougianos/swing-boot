package io.github.swingboot.control.declaration;

import static io.github.swingboot.control.declaration.InstallControlsClassAnalysis.of;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.github.swingboot.control.Control;
import io.github.swingboot.control.InitializedBy;
import io.github.swingboot.control.InstallControls;
import io.github.swingboot.control.ParameterSource;
import io.github.swingboot.control.declaration.InstallControlsClassAnalysisTests.findsMultipleControlDeclaration.VoidControl;
import io.github.swingboot.control.installation.annotation.KeyBinding;
import io.github.swingboot.control.installation.annotation.OnActionPerformed;
import io.github.swingboot.control.installation.annotation.multiple.MultipleKeyBinding;

@SuppressWarnings({ "serial", "unused" })
class InstallControlsClassAnalysisTests {

	@Nested
	public class ExceptionWhenDeclaredSameId {
		@Test
		void main() {
			assertThrows(InvalidControlDeclarationException.class, () -> of(SameId.class));
		}

		@InstallControls
		private class SameId {
			@OnActionPerformed(value = VoidControl.class, id = "someid")
			private JButton button;
			@OnActionPerformed(value = VoidControl.class, id = "someid")
			private JButton button1;
		}
	}

	@Nested
	class findsInitializedBy {
		@Test
		void withParameterSource() throws Exception {
			InstallControlsClassAnalysis classAnalysis = of(InitializedWithParameterSource.class);

			Optional<InitializedByDeclaration> declaration = classAnalysis.getInitializedByDeclaration();
			assertTrue(declaration.get().getParameterSource().isPresent());
		}

		@Test
		void withoutParameterSource() throws Exception {
			InstallControlsClassAnalysis classAnalysis = of(InitializedWithoutParameterSource.class);

			Optional<InitializedByDeclaration> declaration = classAnalysis.getInitializedByDeclaration();
			assertFalse(declaration.get().getParameterSource().isPresent());
		}

		@InstallControls
		@InitializedBy(value = TestControl.class, parameterSource = "someParameter")
		private class InitializedWithParameterSource {

			@ParameterSource("someParameter")
			String parameter() {
				return "";
			}
		}

		@InitializedBy(value = VoidControl.class)
		private class InitializedWithoutParameterSource {

		}
	}

	@Nested
	public class FindsInstallControlFields {
		@Test
		void main() throws Exception {
			InstallControlsClassAnalysis analysis = of(WithNestedInstallControlFields.class);

			Set<Field> nestedFields = analysis.getNestedInstallControlsFields();
			assertEquals(2, nestedFields.size());

			Field targetField1 = WithNestedInstallControlFields.class.getDeclaredField("acceptee");
			Field targetField2 = WithNestedInstallControlFields.class.getDeclaredField("acceptee2");
			assertTrue(nestedFields.contains(targetField1));
			assertTrue(nestedFields.contains(targetField2));

			//Test unmodifiable set
			assertThrows(RuntimeException.class, () -> nestedFields.add(targetField2));
		}

		private class WithNestedInstallControlFields {
			@InstallControls
			private ClassThatDeclaresControls acceptee;
			@InstallControls
			private ClassThatDeclaresControls acceptee2;
		}

		private class ClassThatDeclaresControls {
			@OnActionPerformed(value = VoidControl.class, id = "someid")
			private JButton button;
			@OnActionPerformed(value = VoidControl.class, id = "someid2")
			private JButton button1;
		}
	}

	@Nested
	public class ExceptionWhenDeclaredParamaeterSourceIsAbsent {
		@Test
		void declaredParameterSourceDoesNotExist() {
			assertThrows(InvalidControlDeclarationException.class, () -> of(AbsentParameterSource.class));
		}

		@InstallControls
		private class AbsentParameterSource {
			@OnActionPerformed(value = TestControl.class, parameterSource = "parsource", id = "someid")
			private JButton button;
		}
	}

	@Nested
	public class DeclarationsOnClass {
		@Test
		void normalCaseAnnotationOnType() {
			Class<?> clazz = AnnotationOnClass.class;
			InstallControlsClassAnalysis analysis = of(clazz);
			assertEquals(1, analysis.getControlDeclarations().size());

			ControlInstallationDeclaration declaration = analysis.getControlDeclarations().get("id");
			assertNotNull(declaration);
			assertEquals(clazz, declaration.getTargetElement());
			assertEquals("id", declaration.getId());
			assertTrue(declaration.getParameterSource().isPresent());
			assertEquals("s", declaration.getParameterSource().get().getValue(new AnnotationOnClass(), null));
		}

		@InstallControls
		@OnActionPerformed(id = "id", value = TestControl.class, parameterSource = "parsource")
		private class AnnotationOnClass extends JButton {
			@ParameterSource("parsource")
			private String parSource() {
				return "s";
			}
		}
	}

	@Nested
	class findsMultipleControlDeclaration {
		@InstallControls
		private class MultipleControlDeclarations {
			@OnActionPerformed(value = VoidControl.class, parameterSource = "parsource", id = "someid")
			//@formatter:off
			@MultipleKeyBinding({ 
					@KeyBinding(keyStroke = "F2", value = VoidControl.class, parameterSource = "parsource"),
					@KeyBinding(keyStroke = "F3", value = VoidControl.class, parameterSource = "parsource")
			})
			//@formatter:on
			private JButton button;
		}

		@Test
		void main() {
			InstallControlsClassAnalysis classAnalysis = of(MultipleControlDeclarations.class);
			assertEquals(3, classAnalysis.getControlDeclarations().size());
		}

		@ParameterSource("parsource")
		private String parSource() {
			return "b";
		}

		class VoidControl implements Control<Void> {
			@Override
			public void perform(Void parameter) {
			}
		}
	}

	@Nested
	class DeclarationsOnFields {

		@Test
		void normalCaseOnlyAnnotatedField() throws Exception {
			InstallControlsClassAnalysis analysis = of(NormalCaseWithAnnotatedField.class);
			assertEquals(1, analysis.getControlDeclarations().size());

			ControlInstallationDeclaration declaration = analysis.getControlDeclarations().get("someid");
			assertEquals(NormalCaseWithAnnotatedField.class.getDeclaredField("button"),
					declaration.getTargetElement());
			assertEquals("someid", declaration.getId());
			assertTrue(declaration.getParameterSource().isPresent());
			assertEquals("b", declaration.getParameterSource().get()
					.getValue(new NormalCaseWithAnnotatedField(), null));
		}

		@InstallControls
		private class NormalCaseWithAnnotatedField {
			@Inject
			private JButton injectButton; //Even if annotated, it is not a @DeclaresControl annotation
			@OnActionPerformed(value = TestControl.class, parameterSource = "parsource", id = "someid")
			private JButton button;
			private JPanel zeroAnnotations;

			@ParameterSource("parsource")
			private String parSource() {
				return "b";
			}
		}
	}

	@Test
	void bothFieldAndType() throws Exception {
		InstallControlsClassAnalysis analysis = of(BothFieldAndType.class);
		assertEquals(2, analysis.getControlDeclarations().size());
		assertEquals("parsource", analysis.getControlDeclarations().get("onTypeId").getParameterSourceId());
		assertEquals("parsource", analysis.getControlDeclarations().get("onFieldId").getParameterSourceId());
	}

	@Test
	void inheritanceIgnoreNone() throws Exception {
		InstallControlsClassAnalysis analysis = of(ChildIgnoreNone.class);
		assertEquals(3, analysis.getControlDeclarations().size());
		assertEquals("parsource", analysis.getControlDeclarations().get("onTypeId").getParameterSourceId());
		assertEquals("parsource", analysis.getControlDeclarations().get("onFieldId").getParameterSourceId());
		assertEquals("parsource", analysis.getControlDeclarations().get("childId").getParameterSourceId());
	}

	@Test
	void childDeclaresIdThatExistInParent() throws Exception {
		assertThrows(InvalidControlDeclarationException.class,
				() -> of(ChildDeclaresIdThatExistsOnParent.class));
	}

	@Test
	void childDeclaresIdThatExistInParentButIgnoresParent() throws Exception {
		InstallControlsClassAnalysis analysis = of(ChildDeclaresIdThatExistsOnParentButIgnoresParent.class);
		assertEquals(2, analysis.getControlDeclarations().size());
		assertEquals("parsource", analysis.getControlDeclarations().get("onFieldId").getParameterSourceId());
		assertEquals("parsource", analysis.getControlDeclarations().get("onTypeId").getParameterSourceId());
	}

	@Test
	void childIgnoresAll() throws Exception {
		InstallControlsClassAnalysis analysis = of(ChildIgnoresAll.class);
		assertTrue(analysis.getControlDeclarations().isEmpty());
	}

	@Test
	void cache() {
		InstallControlsClassAnalysis analysis = of(ChildIgnoresAll.class);
		assertSame(analysis, of(ChildIgnoresAll.class));
	}

	@Test
	void withThisParameterSource() throws Exception {
		InstallControlsClassAnalysis analysis = of(ThisParameterSource.class);
		assertEquals(1, analysis.getControlDeclarations().size());

		ControlInstallationDeclaration declaration = analysis.getControlDeclarations().get("someId");
		assertTrue(declaration.getParameterSource().isPresent());

		ThisParameterSource thisParameterSource = new ThisParameterSource();
		assertEquals(thisParameterSource,
				declaration.getParameterSource().get().getValue(thisParameterSource, null));
	}

	@Test
	void grandChildInheritsAll() throws Exception {
		InstallControlsClassAnalysis analysis = of(GrandChild.class);
		assertEquals(4, analysis.getControlDeclarations().size());
		assertNotNull(analysis.getControlDeclarations().get("onTypeId"));
		assertNotNull(analysis.getControlDeclarations().get("onFieldId"));
		assertNotNull(analysis.getControlDeclarations().get("childId"));
		assertNotNull(analysis.getControlDeclarations().get("childId2"));
	}

	@Test
	void grandChildIgnoresGrandParent() throws Exception {
		InstallControlsClassAnalysis analysis = of(GrandChildIgnoresGrandParent.class);
		assertEquals(2, analysis.getControlDeclarations().size());
		assertNotNull(analysis.getControlDeclarations().get("onFieldId"));
		assertNotNull(analysis.getControlDeclarations().get("childId"));
	}

	@Test
	void grandChildIgnoresAll() throws Exception {
		InstallControlsClassAnalysis analysis = of(GrandChildIgnoresAll.class);
		assertEquals(0, analysis.getControlDeclarations().size());
	}

	private static class TestControl implements Control<String> {
		@Override
		public void perform(String parameter) {
		}
	}

	@InstallControls
	@OnActionPerformed(value = TestControl.class, parameterSource = "parsource", id = "onTypeId")
	private static class BothFieldAndType extends JButton {
		//Static fields should be ignored
		@OnActionPerformed(value = TestControl.class, parameterSource = "parsource", id = "onFieldId")
		private static JButton staticButton;
		@OnActionPerformed(value = TestControl.class, parameterSource = "parsource", id = "onFieldId")
		private JButton button;

		@ParameterSource("parsource")
		private String parSource() {
			return "s";
		}
	}

	@InstallControls
	private static class ChildIgnoreNone extends BothFieldAndType {
		@OnActionPerformed(value = TestControl.class, parameterSource = "parsource", id = "childId")
		private JButton button;
	}

	@InstallControls(ignoreIdsFromParent = "onTypeId")
	private static class GrandChildIgnoresGrandParent extends ChildIgnoreNone {

	}

	@InstallControls(ignoreAllIdsFromParent = true)
	private static class GrandChildIgnoresAll extends ChildIgnoreNone {

	}

	@InstallControls
	private static class GrandChild extends ChildIgnoreNone {
		@OnActionPerformed(value = TestControl.class, parameterSource = "parsource", id = "childId2")
		private JButton button;
	}

	@InstallControls
	private static class ChildDeclaresIdThatExistsOnParent extends BothFieldAndType {
		@OnActionPerformed(value = TestControl.class, parameterSource = "parsource", id = "onFieldId")
		private JButton button;
	}

	@InstallControls(ignoreIdsFromParent = "onFieldId")
	private static class ChildDeclaresIdThatExistsOnParentButIgnoresParent extends BothFieldAndType {
		@OnActionPerformed(value = TestControl.class, parameterSource = "parsource", id = "onFieldId")
		private JButton button;
	}

	@InstallControls(ignoreAllIdsFromParent = true)
	private static class ChildIgnoresAll extends BothFieldAndType {

	}

	@InstallControls
	private static class ThisParameterSource {
		@OnActionPerformed(value = ControlWithThisParameterSourceGenericType.class, parameterSource = ParameterSource.THIS, id = "someId")
		private JButton button;
	}

	private static class ControlWithThisParameterSourceGenericType implements Control<ThisParameterSource> {
		@Override
		public void perform(ThisParameterSource parameter) {
		}
	}
}
