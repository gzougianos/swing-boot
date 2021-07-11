package example;

import javax.annotation.Nullable;

import io.github.swingboot.control.Control;

public class PrintResizeControl implements Control<Integer> {

	@Override
	public void perform(@Nullable Integer parameter) {
		System.out.println("print resize");
	}

}
