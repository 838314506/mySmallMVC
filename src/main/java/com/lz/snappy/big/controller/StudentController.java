package com.lz.snappy.big.controller;

import com.lz.snappy.big.anno.MyController;
import com.lz.snappy.big.anno.MyRequestMapping;

@MyController
@MyRequestMapping("/student")
public class StudentController {
	
	@MyRequestMapping("/tom")
	public String tom() {
		return "this is tom home";
	}

}
