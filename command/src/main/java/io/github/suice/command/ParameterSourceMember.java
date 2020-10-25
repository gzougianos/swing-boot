package io.github.suice.command;

import static io.github.suice.command.reflect.ReflectionUtils.equalsOrExtends;

import java.awt.AWTEvent;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public class ParameterSourceMember {

	private Object memberOwner;
	private Member member;

	public ParameterSourceMember(Object memberOwner, Member member) {
		this.memberOwner = memberOwner;
		this.member = member;
	}

	public Member getMember() {
		return member;
	}

	public Object getMemberOwner() {
		return memberOwner;
	}

	public Class<?> getReturnType() {
		if (member instanceof Field)
			return ((Field) member).getType();
		if (member instanceof Method)
			return ((Method) member).getReturnType();
		return null;
	}

	public Object getValue(AWTEvent event) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (member instanceof Field) {
			Field field = (Field) member;
			field.setAccessible(true);
			return field.get(memberOwner);
		} else if (member instanceof Method) {
			Method method = (Method) member;
			method.setAccessible(true);
			if (method.getParameterCount() == 1) {
				return invokeMethodWithParameter(method, event);
			}
			return method.invoke(memberOwner);
		}
		//should never happend. member is always field or method
		return null;
	}

	private Object invokeMethodWithParameter(Method method, AWTEvent event)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class<?> methodParameterType = method.getParameterTypes()[0];
		if (equalsOrExtends(event.getClass(), methodParameterType)) {
			return method.invoke(memberOwner, event);
		}
		return method.invoke(memberOwner, (Object) null);
	}

	@Override
	public String toString() {
		return "ParameterSourceMember [memberOwner=" + memberOwner + ", member=" + member + "]";
	}

}
