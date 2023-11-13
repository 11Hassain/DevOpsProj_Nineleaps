package com.example.devopsproj;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

@SpringBootTest
class DevOpsProjApplicationTest {

        @Test
        void contextLoads() {
                boolean condition = true;
                Assertions.assertTrue(condition, "Test class is being loaded successfully");
        }

//        @Test
//        void testMainMethod() {
//                DevOpsProjApplication.main(new String[]{});
//                Assertions.assertTrue(true, "Main method tested");
//        }

}

