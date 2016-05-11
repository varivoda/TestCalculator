package com.varivoda.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.qatools.allure.annotations.Parameter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by ivan on 06.05.16.
 * Тест выполняет проверку операций, считанных из файла
 * при этом все числовые данные представляются типом long,
 * что позволяет обрабатывать большие целые значения.
 */
@RunWith(Parameterized.class)
public class CalculatorIntegerTest {

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


    /**
     * Метод возвращает поток ввода по имени файла, расположенного в src с тестами
     */
    private static InputStream getInputStreamFromFile(String fileName){
        return CalculatorIntegerTest.class.getClassLoader().getResourceAsStream(fileName);
    }

    /**
     * Метод возвращает поток ввода по абсолютному пути файла
     * @param path - путь к файлу
     * @throws IOException
     */
    private static InputStream getFileInputStreamByPath(String path) throws IOException {
        return Files.newInputStream(Paths.get(path));
    }

    //Метод возвращает список массивов параметров, считанных из файла
    @Parameterized.Parameters
    public static Collection<Object[]> dataProviderFromFile(){

        /*
        Список массивов длины 4.
        Каждому массиву соответствует строчка из файла
        На первом месте данные операнда 1
        На втором данные операнда 2
        На третьем строка, соответствующая оператору + - / *
        На четвертом данные результата для проверки
        */
        List<Object[]> resultObjectList = new LinkedList<>();

        try(BufferedReader br  = new BufferedReader(new InputStreamReader(getInputStreamFromFile("dataFile.txt")))) {

            String line;
                /*
                Считываем все строчки из файла по очереди
                Преобразуем в массив по правилу:  разделитель: ";"
                Записываем массивы в результирующий список
                 */
            while( (line = br.readLine()) != null)
            {
                resultObjectList.add(line.split(";"));
            }
        }
        // Отлавливаем ошибку, которая может возникнуть при работе с файлом
        catch (IOException e) {
            e.printStackTrace();
        }

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
