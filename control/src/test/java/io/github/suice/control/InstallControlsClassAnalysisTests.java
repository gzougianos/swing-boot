package io.github.suice.control;

import static io.github.suice.control.InstallControlsClassAnalysis.of;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.inject.Inject;
import javax.swing.JButton;

import org.junit.jupiter.api.Test;

import io.github.suice.control.annotation.InstallControls;
import io.github.suice.control.annotation.ParameterSource;
import io.github.suice.control.annotation.listener.OnActionPerformed;

@SuppressWarnings("all")
class InstallControlsClassAnalysisTests {

	@Test
	void exceptionIfItIsNotAnnotatedWithInstallControlsAnnotation() {
		assertThrows(IllegalArgumentException.class, () -> of(WithoutInstallControlsAnnotation.class));
	}

	@Test
	void exceptionWhenSameId() {
		assertThrows(InvalidControlDeclarationException.class, () -> of(DeclaredSameId.class));
	}

	@Test
	void declaredParameterSourceDoesNotExist() {
		assertThrows(InvalidControlDeclarationException.class, () -> of(DeclaresParameterSourceButDoesNotExist.class));
	}

	@Test
	void normalCaseAnnotationOnType() {
		Class<?> clazz = AnnotationOnType.class;
		InstallControlsClassAnalysis analysis = of(clazz);
		assertEquals(1, analysis.getControlDeclarations().size());

		String expectedId = clazz.getAnnotation(OnActionPerformed.class) + clazz.toString();
		ControlDeclaration declaration = analysis.getControlDeclarations().get(expectedId);
		assertNotNull(declaration);
		assertEquals(clazz, declaration.getTargetElement());
		assertEquals(expectedId, declaration.getId());
		assertTrue(declaration.getParameterSource().isPresent());
		assertEquals("s", declaration.getParameterSource().get().getValue(new AnnotationOnType(), null));
	}

	@Test
	void normalCaseOnlyAnnotatedField() throws Exception {
		InstallControlsClassAnalysis analysis = of(NormalCaseWithAnnotatedField.class);
		assertEquals(1, analysis.getControlDeclarations().size());

		ControlDeclaration declaration = analysis.getControlDeclarations().get("someid");
		assertEquals(NormalCaseWithAnnotatedField.class.getDeclaredField("button"), declaration.getTargetElement());
		assertEquals("someid", declaration.getId());
		assertTrue(declaration.getParameterSource().isPresent());
		assertEquals("b", declaration.getParameterSource().get().getValue(null, null));
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
		assertThrows(InvalidControlDeclarationException.class, () -> of(ChildDeclaresIdThatExistsOnParent.class));
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

		ControlDeclaration declaration = analysis.getControlDeclarations().get("someId");
		assertTrue(declaration.getParameterSource().isPresent());

		ThisParameterSource thisParameterSource = new ThisParameterSource();
		assertEquals(thisParameterSource, declaration.getParameterSource().get().getValue(thisParameterSource, null));
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

	private static class WithoutInstallControlsAnnotation {
	}

	@InstallControls
	private static class DeclaredSameId {
		@OnActionPerformed(value = TestControl.class, id = "someid")
		private JButton button;
		@OnActionPerformed(value = TestControl.class, id = "someid")
		private JButton button1;
	}

	@InstallControls
	private static class NormalCaseWithAnnotatedField {
		private static JButton staticButton;//checkIfIgnored
		@Inject
		private JButton injectButton; //Even if annotated, it is not a @DeclaresControl annotation
		@OnActionPerformed(value = TestControl.class, parameterSource = "parsource", id = "someid")
		private JButton button;

		@OnActionPerformed(TestControl.class)
		private int aNoComponentField; //should be ignored

		@ParameterSource("parsource")
		private static String parSource() {
			return "b";
		}
	}

	@InstallControls
	@OnActionPerformed(value = TestControl.class, parameterSource = "parsource")
	private static class AnnotationOnType extends JButton {
		@ParameterSource("parsource")
		private String parSource() {
			return "s";
		}
	}

	private static class TestControl implements Control<String> {
		@Override
		public void perform(String parameter) {
		}
	}

	@InstallControls
	private static class DeclaresParameterSourceButDoesNotExist {
		@OnActionPerformed(value = TestControl.class, parameterSource = "parsource", id = "someid")
		private JButton button;
	}

	@InstallControls
	@OnActionPerformed(value = TestControl.class, parameterSource = "parsource", id = "onTypeId")
	private static class BothFieldAndType extends JButton {
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
