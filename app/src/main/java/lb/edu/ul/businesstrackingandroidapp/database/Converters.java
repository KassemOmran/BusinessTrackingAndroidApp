package lb.edu.ul.businesstrackingandroidapp.database;

import androidx.room.TypeConverter;

import java.time.LocalDate;

public class Converters {

    @TypeConverter
    public static String fromLocalDate(LocalDate date) {
        return date == null ? null : date.toString();
    }

    @TypeConverter
    public static LocalDate toLocalDate(String value) {
        return value == null ? null : LocalDate.parse(value);
    }



        @TypeConverter
        public static String fromOrderType(OrderType type) {
            return type == null ? null : type.name();
        }

        @TypeConverter
        public static OrderType toOrderType(String value) {
            return value == null ? null : OrderType.valueOf(value);
        }




    }

