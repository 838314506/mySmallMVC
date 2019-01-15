package com.lz.snappy;

import com.lz.snappy.big.anno.MyController;
import com.lz.snappy.big.anno.MyRequestMapping;
import com.lz.snappy.big.controller.StudentController;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	Class<? extends StudentController> clazz = new StudentController().getClass();
    	MyController annotation = clazz.getAnnotation(MyController.class);
    	System.out.println(annotation);
    	MyRequestMapping anno = clazz.getAnnotation(MyRequestMapping.class);
        System.out.println( anno.value() );
    }
}
