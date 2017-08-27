package org.github.pulsar929.virtualKeyboard;

import org.junit.Test;

public class VirtualKeyboardTest {

	@Test
	public void test() {
		VirtualKeyboard vk = new VirtualKeyboard();
		System.out.println(vk.getPath("QWERTYUIBPAS", 5, 'B', "BAR"));
		System.out.println(vk.getPath("RTYASDEUIOL", 3, 'Y', "TILT"));
		System.out.println(vk.getPath("RTYASDEUIOL", 3, 'R', "LLLL"));
		System.out.println(vk.getPath("RTYASDEUIOL", 3, 'R', "O"));
		System.out.println(vk.getPath("RTYASDEUIOL", 3, 'R', "Y"));
		System.out.println(vk.getPath("RTYASDEUIOL", 3, 'R', "L"));
		System.out.println(vk.getPath("RTYASDEUIOL", 3, 'R', "I"));
	}

}
