package com.trackmybus.service;

import com.trackmybus.model.*;
import java.util.*;

/** Static data of the college bus network (stops and routes). Edit here to match your transport office. */
public final class DataSet {
    private static final String STOPS = """
Thrissur,10.527,76.214,T
Ayyanthole,10.54,76.2,T
Puranattukara,10.55,76.2,T
Mannuthy,10.543,76.265,T
Ollur,10.48,76.25,T
Wadakkanchery,10.655,76.255,T
Cheruthuruthy,10.7,76.28,T
Jyothi Engineering College,10.722,76.282,T
Chelakkara,10.69,76.34,T
Pazhayannur,10.66,76.4,T
Kunnamkulam,10.65,76.07,T
Erumapetty,10.65,76.12,T
Guruvayur,10.594,76.04,T
Chavakkad,10.57,76.02,T
Anthikad,10.45,76.12,T
Peringottukara,10.41,76.11,T
Arimpur,10.44,76.17,T
Puthukkad,10.41,76.27,T
Kodakara,10.37,76.29,T
Irinjalakuda,10.34,76.21,T
Chalakudy,10.3,76.34,T
Mala,10.24,76.26,T
Kodungallur,10.23,76.19,T
Palakkad,10.775,76.651,P
Kannadi,10.74,76.56,P
Mundur,10.79,76.55,P
Kodumba,10.8,76.62,P
Malampuzha,10.83,76.68,P
Kanjikode,10.8,76.75,P
Walayar,10.84,76.86,P
Chittur,10.7,76.75,P
Kollengode,10.61,76.69,P
Nenmara,10.6,76.6,P
Alathur,10.64,76.54,P
Kuzhalmannam,10.66,76.5,P
Vadakkencherry,10.6,76.48,P
Ottappalam,10.77,76.38,P
Vaniyamkulam,10.76,76.31,P
Shoranur,10.76,76.27,P
Cherpulassery,10.88,76.31,P
Mannarkkad,10.99,76.46,P
Pattambi,10.81,76.19,P
Koottanad,10.81,76.13,P
Thrithala,10.8,76.09,P
""";
    private static final String ROUTES = """
Bus 10|Palakkad,Kannadi,Alathur,Vadakkencherry,Pazhayannur,Chelakkara,Cheruthuruthy,Jyothi Engineering College|0
Bus 11|Palakkad,Kollengode,Nenmara,Kuzhalmannam,Vadakkencherry,Pazhayannur,Chelakkara,Jyothi Engineering College|3
Bus 12|Chittur,Kollengode,Nenmara,Alathur,Vadakkencherry,Pazhayannur,Chelakkara,Jyothi Engineering College|6
Bus 13|Mannarkkad,Cherpulassery,Ottappalam,Vaniyamkulam,Shoranur,Cheruthuruthy,Jyothi Engineering College|2
Bus 14|Thrithala,Koottanad,Pattambi,Shoranur,Cheruthuruthy,Jyothi Engineering College|5
Bus 15|Thrissur,Ayyanthole,Mannuthy,Wadakkanchery,Cheruthuruthy,Jyothi Engineering College|1
Bus 16|Chavakkad,Guruvayur,Kunnamkulam,Erumapetty,Wadakkanchery,Cheruthuruthy,Jyothi Engineering College|4
Bus 17|Kodungallur,Mala,Chalakudy,Kodakara,Puthukkad,Ollur,Thrissur,Mannuthy,Wadakkanchery,Jyothi Engineering College|7
Bus 18|Anthikad,Peringottukara,Arimpur,Irinjalakuda,Puthukkad,Ollur,Puranattukara,Mannuthy,Wadakkanchery,Jyothi Engineering College|9
""";

    private DataSet() {}

    public static List<Stop> stops() {
        List<Stop> list = new ArrayList<>();
        for (String line : STOPS.strip().split("\n")) {
            String[] p = line.split(",");
            list.add(new Stop(list.size(), p[0], Double.parseDouble(p[1]), Double.parseDouble(p[2]), p[3]));
        }
        return list;
    }

    /** Each entry: [busNumber, comma separated stop names, simulation offset]. */
    public static List<String[]> routes() {
        List<String[]> list = new ArrayList<>();
        for (String line : ROUTES.strip().split("\n")) list.add(line.split("\\|"));
        return list;
    }
}
