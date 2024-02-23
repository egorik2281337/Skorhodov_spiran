package org.example;

import org.example.searadar.mr231.convert.Mr231Converter;
import org.example.searadar.mr231.station.Mr231StationType;

import org.example.searadar.mr231_3.convert.Mr231_3Converter;
import org.example.searadar.mr231_3.station.Mr231_3StationType;
import ru.oogis.searadar.api.message.SearadarStationMessage;

import java.util.List;

// Задача написать парсер сообщений для протокола МР-231-3 на основе парсера МР-231.
 

public class App {
    public static void main(String[] args) {
        // Контрольный пример для МР-231
        String mr231_TTM = "$RATTM,66,28.71,341.1,T,57.6,024.5,T,0.4,4.1,N,b,L,,457362,А*42";

        // Проверка работы конвертера
        Mr231StationType mr231 = new Mr231StationType();
        Mr231Converter converter = mr231.createConverter();
        List<SearadarStationMessage> searadarMessages = converter.convert(mr231_TTM);
        System.out.println("Контрольный пример для МР-231:");
        searadarMessages.forEach(System.out::println);

         // Контрольный пример для МР-231-3
         String mr231_3_TTM = "$1RA2TTM3,01,10.5,180.0,T,20.0,090.0,T,15.0,120.0,N,b,L,,123456,A*78";

        // Проверка работы конвертера МР-231-3
        Mr231_3Converter converter_3 = new Mr231_3Converter();
        List<SearadarStationMessage> searadarMessages_3 = converter_3.convert(mr231_3_TTM);
         System.out.println("Контрольный пример для МР-231-3:");
        searadarMessages_3.forEach(System.out::println);
         }
     }
