package org.cm.podd.urban.report.data;

import java.util.List;

/**
 * Created by nat on 10/29/15 AD.
 */
public class AdministrationArea {
    public static class Model {


        /**
         * id : 26
         * name : เทศบาลตำบลยางเนิ้ง
         * parentName : อำเภอสารภี
         * isLeaf : true
         * address : เทศบาลตำบลยางเนิ้ง อำเภอสารภี
         * location : {"type":"Point","coordinates":[99.039693,18.705308]}
         * code : 1901a
         * authority : 73
         * canEdit : true
         */

        public int id;
        public String name;
        public String parentName;
        public boolean isLeaf;
        public String address;
        public int weight;
        /**
         * type : Point
         * coordinates : [99.039693,18.705308]
         */


        public LocationEntity location;
        public String code;
        public int authority;
        public boolean canEdit;


        public static class LocationEntity {
            public String type;
            public List<Double> coordinates;

        }

        public HotReport.Model.PolygonEntity mpoly;



    }

}
