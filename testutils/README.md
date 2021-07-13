# A small extension for JUnit 5 to run tests in the [EDT](https://docs.oracle.com/javase/tutorial/uiswing/concurrency/dispatch.html)

### Dependency:

```
<dependency>
  <groupId>io.github.swingboot</groupId>
  <artifactId>testutils</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <scope>test</scope>
</dependency> 
```

### @UiTest

**Replaces** the ordinary `@Test` upon the test method and runs the test method in the EDT:
```
@ExtendWith(UiExtension.class)
class TestClass {

	@UiTest
	void test() {
		assertTrue(SwingUtilities.isEventDispatchThread());
	}
}
  ```
  
  
### @UiAll

Can be used on the top of the class to run all test methods of the test class in the EDT. Then the ordinary `Test` can be used.

```
@ExtendWith(UiExtension.class)
@UiAll
class TestClass {

	@Test
	void test() {
		assertTrue(SwingUtilities.isEventDispatchThread());
	}
}
```

`@UiAll` can also exlude some of the test methods. For example, you can exlude the `@BeforeAll` method.

```
@ExtendWith(UiExtension.class)
@UiAll(exclude = { BeforeAll.class })
class TestClass {

	@Test
	void test() {
		assertTrue(SwingUtilities.isEventDispatchThread());
	}

	@BeforeAll
	static void ini() {
		assertFalse(SwingUtilities.isEventDispatchThread());
	}
}
```





