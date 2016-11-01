package mylibrary.helper;


import mylibrary.Utils;

public class TimeUtils {

    public final static long ONE_SECOND = 1000;
    public final static long SECONDS = 60;

    public final static long ONE_MINUTE = ONE_SECOND * 60;
    public final static long MINUTES = 60;

    public final static long ONE_HOUR = ONE_MINUTE * 60;
    public final static long HOURS = 24;

    public final static long ONE_DAY = ONE_HOUR * 24;

    private TimeUtils() {
    }

    /**
     * converts time (in milliseconds) to human-readable format
     *  "<w> days, <x> hours, <y> minutes and (z) seconds"
     */
    public static String millisToLongDHMS(long duration) {
        StringBuffer res = new StringBuffer();
        long temp = 0;
        int counter = 0;
        if (duration >= ONE_SECOND) {
            temp = duration / ONE_DAY;
            if (temp > 0 && counter < 1) {
                duration -= temp * ONE_DAY;
                if (Utils.isThaiVersion()) {
                    res.append(temp).append(" วัน");
                }
                else {
                    res.append(temp).append(" day").append(temp > 1 ? "s" : "")
                            .append(duration >= ONE_MINUTE ? "" : "");
                }
                counter ++;
            }

            temp = duration / ONE_HOUR;
            if (temp > 0  && counter < 1) {
                duration -= temp * ONE_HOUR;
                if (Utils.isThaiVersion()) {
                    res.append(temp).append(" ชั่วโมง");
                }
                else {
                    res.append(temp).append(" hour").append(temp > 1 ? "s" : "")
                            .append(duration >= ONE_MINUTE ? "" : "");
                }
                counter ++;
            }

            temp = duration / ONE_MINUTE;
            if (temp > 0 && counter < 1) {
                duration -= temp * ONE_MINUTE;
                if (Utils.isThaiVersion()) {
                    res.append(temp).append(" นาที");
                }
                else {
                    res.append(temp).append(" minute").append(temp > 1 ? "s" : "");
                }
                counter ++;
            }

//            if (!res.toString().equals("") && duration >= ONE_SECOND && counter < 2) {
//                res.append(" and ");
//            }

            temp = duration / ONE_SECOND;
            if (temp > 0 && counter < 1) {
                if (Utils.isThaiVersion()) {
                    res.append(temp).append(" วินาที");
                }
                else {
                    res.append(temp).append(" second").append(temp > 1 ? "s" : "");
                }
            }
            return res.toString();
        } else {
            if (Utils.isThaiVersion()) {
                return "0 วินาที";
            }
            else {
                return "0 second";
            }
        }
    }
}
