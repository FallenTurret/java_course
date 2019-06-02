package ru.hse.myjunit.example;

import ru.hse.myjunit.*;

public class Example {

    public int bcExpected = 2;
    public int acExpected = 2;
    public int bExpected = 3;
    public int aExpected = 3;
    public int tExpected = 3;

    public int bc = 0;
    public int ac = 0;
    public int b = 0;
    public int a = 0;
    public int t = 0;

    @BeforeClass
    void before1() {
        bc++;
    }

    @BeforeClass
    void before2() {
        bc++;
    }

    @AfterClass
    void after1() {
        ac++;
    }

    @AfterClass
    void after2() {
        ac++;
    }

    @Before
    synchronized void before() {
        b++;
    }

    @After
    synchronized void after() {
        a++;
    }

    @Test(expected = ArithmeticException.class)
    synchronized void test1() {
        t++;
        int x = 1 / 0;
    }

    @Test(ignore = "Some reason")
    synchronized void test2() {
        t++;
    }

    @Test
    synchronized void test3() {
        int d = 1;
        for (int i = 1; i < 1000000; i++) {
            d *= i;
        }
        t++;
    }

    @Test
    synchronized void test4() {
        t++;
        assert(0 == 1);
    }

}