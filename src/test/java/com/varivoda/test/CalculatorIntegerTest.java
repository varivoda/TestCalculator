package com.varivoda.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.qatools.allure.annotations.Parameter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * Created by ivan on 06.05.16.
 * Тест выполняет проверку операций, считанных из файла,
 * который задается абсолютным путем PATH_DATA_FILE
 * при этом все числовые данные представляются типом long,
 * что позволяет обрабатывать большие целые значения.
 */
@RunWith(Parameterized.class)
public class CalculatorIntegerTest {

    public static final String PATH_DATA_FILE = "/home/ivan/Desktop/test";

    @Parameter("Operand 1")
    private String operand1Str;
    @Parameter("Operand 2")
    private String operand2Str;
    @Parameter("Operator")
    private String operatorStr;
    @Parameter("Result")
    private String resultStr;

    public CalculatorIntegerTest(String operand1Str, String operand2Str, String operatorStr, String resultStr) {
        this.operand1Str = operand1Str;
        this.operand2Str = operand2Str;
        this.operatorStr = operatorStr;
        this.resultStr = resultStr;
    }


    //Метод возвращает список массивов параметров, считанных из файла
    @Parameterized.Parameters
    public static Collection<Object[]> dataProviderFromFile(){

        Stream<String> fileLinesStream = null;
        try {
            fileLinesStream = Files.lines(Paths.get(PATH_DATA_FILE));
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        resultObjectList - Список массивов длины 4.
        Каждому массиву соответствует строчка из файла
        На первом месте данные операнда 1
        На втором данные операнда 2
        На третьем строка, соответствующая оператору + - / *
        На четвертом данные результата для проверки
        */
        List<Object[]> resultObjectList = null;

        if (fileLinesStream != null) {
            resultObjectList = fileLinesStream.map(s -> s.split(";")).collect(Collectors.toList());
        }

//        resultObjectList.stream().map(Arrays::toString).forEach(System.out::println);

        return  resultObjectList;
    }


    @Test
    public void calculateTest(){


        long operand1, operand2, expectedResult, actualResult;
        String operator;

        /*
        Парсим параметры.
        При возникновении ошибки парсера проваливаем тест с выводом сообщениия о некорректных параметрах.
        Long.parseLong кидает NumberFormatException который является наследником IllegalArgumentException,
        который мы отлавливаем.
        */
        try {
            operand1 = Long.parseLong(operand1Str);
            operand2 = Long.parseLong(operand2Str);
            operator = operatorStr;
            expectedResult = Long.parseLong(resultStr);

            //Вычисление результата и проверка
            actualResult = operation(operand1, operand2, operator);
            assertEquals(actualResult, expectedResult);
        }
        catch (IllegalArgumentException e) {
            assertFalse("One of the input parameters is invalid", true);
        }
    }

    /**
     * @param operand1
     * @param operand2
     * @param operator строка, претендующая на звание оператора
     * @return возвращает результат операции
     * @throws IllegalArgumentException метод кидает исключение, если параметр operator не является одним из + - * /
     */
    private static long operation(long operand1, long operand2, String operator) throws IllegalArgumentException{
        switch (operator){
            case "+": return operand1 + operand2;
            case "*": return operand1 * operand2;
            case "-": return operand1 - operand2;
            case "/": return operand1 / operand2;
            default: throw new IllegalArgumentException();
        }
    }
}
