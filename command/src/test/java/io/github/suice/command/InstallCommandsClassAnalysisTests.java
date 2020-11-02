package io.github.suice.command;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.inject.Inject;
import javax.swing.JButton;

import org.junit.jupiter.api.Test;

import io.github.suice.command.annotation.OnActionPerformed;
import io.github.suice.command.annotation.ParameterSource;
import io.github.suice.command.exception.InvalidCommandDeclarationException;

@SuppressWarnings("all")
class InstallCommandsClassAnalysisTests {

	@Test
	void exceptionIfItIsNotAnnotatedWithInstallCommandsAnnotation() {
		assertThrows(IllegalArgumentException.class,
				() -> new InstallCommandsClassAnalysis(WithoutInstallCommandsAnnotation.class));
	}

	@Test
	void exceptionWhenSameId() {
		assertThrows(InvalidCommandDeclarationException.class, () -> new InstallCommandsClassAnalysis(DeclaredSameId.class));
	}

	@Test
	void declaredParameterSourceDoesNotExist() {
		assertThrows(InvalidCommandDeclarationException.class,
				() -> new InstallCommandsClassAnalysis(DeclaresParameterSourceButDoesNotExist.class));
	}

	@Test
	void normalCaseAnnotationOnType() {
		Class<?> clazz = AnnotationOnType.class;
		InstallCommandsClassAnalysis analysis = new InstallCommandsClassAnalysis(clazz);
		assertEquals(1, analysis.getCommandDeclarations().size());

		String expectedId = clazz.getAnnotation(OnActionPerformed.class) + clazz.toString();
		CommandDeclaration declaration = analysis.getCommandDeclarations().get(expectedId);
		assertNotNull(declaration);
		assertEquals(clazz, declaration.getTargetElement());
		assertEquals(expectedId, declaration.getId());
		assertTrue(declaration.getParameterSource().isPresent());
		assertEquals("s", declaration.getParameterSource().get().getValue(new AnnotationOnType(), null));
	}

	@Test
	void normalCaseOnlyAnnotatedField() throws Exception {
		InstallCommandsClassAnalysis analysis = new InstallCommandsClassAnalysis(NormalCaseWithAnnotatedField.class);
		assertEquals(1, analysis.getCommandDeclarations().size());

		CommandDeclaration declaration = analysis.getCommandDeclarations().get("someid");
		assertEquals(NormalCaseWithAnnotatedField.class.getDeclaredField("button"), declaration.getTargetElement());
		assertEquals("someid", declaration.getId());
		assertTrue(declaration.getParameterSource().isPresent());
		assertEquals("b", declaration.getParameterSource().get().getValue(null, null));
	}

	@Test
	void bothFieldAndType() throws Exception {
		InstallCommandsClassAnalysis analysis = new InstallCommandsClassAnalysis(BothFieldAndType.class);
		assertEquals(2, analysis.getCommandDeclarations().size());
		assertEquals("parsource", analysis.getCommandDeclarations().get("onTypeId").getParameterSourceId());
		assertEquals("parsource", analysis.getCommandDeclarations().get("onFieldId").getParameterSourceId());
	}

	@Test
	void inheritanceIgnoreNone() throws Exception {
		InstallCommandsClassAnalysis analysis = new InstallCommandsClassAnalysis(ChildIgnoreNone.class);
		assertEquals(3, analysis.getCommandDeclarations().size());
		assertEquals("parsource", analysis.getCommandDeclarations().get("onTypeId").getParameterSourceId());
		assertEquals("parsource", analysis.getCommandDeclarations().get("onFieldId").getParameterSourceId());
		assertEquals("parsource", analysis.getCommandDeclarations().get("childId").getParameterSourceId());
	}

	@Test
	void childDeclaresIdThatExistInParent() throws Exception {
		assertThrows(InvalidCommandDeclarationException.class,
				() -> new InstallCommandsClassAnalysis(ChildDeclaresIdThatExistsOnParent.class));
	}

	@Test
	void childDeclaresIdThatExistInParentButIgnoresParent() throws Exception {
		InstallCommandsClassAnalysis analysis = new InstallCommandsClassAnalysis(
				ChildDeclaresIdThatExistsOnParentButIgnoresParent.class);
		assertEquals(2, analysis.getCommandDeclarations().size());
		assertEquals("parsource", analysis.getCommandDeclarations().get("onFieldId").getParameterSourceId());
		assertEquals("parsource", analysis.getCommandDeclarations().get("onTypeId").getParameterSourceId());
	}

	@Test
	void childIgnoresAll() throws Exception {
		InstallCommandsClassAnalysis analysis = new InstallCommandsClassAnalysis(ChildIgnoresAll.class);
		assertTrue(analysis.getCommandDeclarations().isEmpty());
	}

	@Test
	void withThisParameterSource() throws Exception {
		InstallCommandsClassAnalysis analysis = new InstallCommandsClassAnalysis(ThisParameterSource.class);
		assertEquals(1, analysis.getCommandDeclarations().size());

		CommandDeclaration declaration = analysis.getCommandDeclarations().get("someId");
		assertTrue(declaration.getParameterSource().isPresent());

		ThisParameterSource thisParameterSource = new ThisParameterSource();
		assertEquals(thisParameterSource, declaration.getParameterSource().get().getValue(thisParameterSource, null));
	}

	private static class WithoutInstallCommandsAnnotation {
	}

	@InstallCommands
	private static class DeclaredSameId {
		@OnActionPerformed(value = TestCommand.class, id = "someid")
		private JButton button;
		@OnActionPerformed(value = TestCommand.class, id = "someid")
		private JButton button1;
	}

	@InstallCommands
	private static class NormalCaseWithAnnotatedField {
		private static JButton staticButton;//checkIfIgnored
		@Inject
		private JButton injectButton; //Even if annotated, it is not a @DeclaresCommand annotation
		@OnActionPerformed(value = TestCommand.class, parameterSource = "parsource", id = "someid")
		private JButton button;

		@OnActionPerformed(TestCommand.class)
		private int aNoComponentField; //should be ignored

		@ParameterSource("parsource")
		private static String parSource() {
			return "b";
		}
	}

	@InstallCommands
	@OnActionPerformed(value = TestCommand.class, parameterSource = "parsource")
	private static class AnnotationOnType extends JButton {
		@ParameterSource("parsource")
		private String parSource() {
			return "s";
		}
	}

	private static class TestCommand implements Command<String> {
		@Override
		public void execute(String parameter) {
		}
	}

	@InstallCommands
	private static class DeclaresParameterSourceButDoesNotExist {
		@OnActionPerformed(value = TestCommand.class, parameterSource = "parsource", id = "someid")
		private JButton button;
	}

	@InstallCommands
	@OnActionPerformed(value = TestCommand.class, parameterSource = "parsource", id = "onTypeId")
	private static class BothFieldAndType extends JButton {
		@OnActionPerformed(value = TestCommand.class, parameterSource = "parsource", id = "onFieldId")
		private JButton button;

		@ParameterSource("parsource")
		private String parSource() {
			return "s";
		}
	}

	@InstallCommands
	private static class ChildIgnoreNone extends BothFieldAndType {
		@OnActionPerformed(value = TestCommand.class, parameterSource = "parsource", id = "childId")
		private JButton button;
	}

	@InstallCommands
	private static class ChildDeclaresIdThatExistsOnParent extends BothFieldAndType {
		@OnActionPerformed(value = TestCommand.class, parameterSource = "parsource", id = "onFieldId")
		private JButton button;
	}

	@InstallCommands(ignoreIdsFromParent = "onFieldId")
	private static class ChildDeclaresIdThatExistsOnParentButIgnoresParent extends BothFieldAndType {
		@OnActionPerformed(value = TestCommand.class, parameterSource = "parsource", id = "onFieldId")
		private JButton button;
	}

	@InstallCommands(ignoreAllIdsFromParent = true)
	private static class ChildIgnoresAll extends BothFieldAndType {

	}

	@InstallCommands
	private static class ThisParameterSource {
		@OnActionPerformed(value = CommandWithThisParameterSourceGenericType.class, parameterSource = ParameterSource.THIS, id = "someId")
		private JButton button;
	}

	private static class CommandWithThisParameterSourceGenericType implements Command<ThisParameterSource> {
		@Override
		public void execute(ThisParameterSource parameter) {
		}
	}
}
