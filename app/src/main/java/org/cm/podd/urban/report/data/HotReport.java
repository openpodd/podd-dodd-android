package org.cm.podd.urban.report.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by nat on 10/29/15 AD.
 */
public class HotReport {

    public static class Model {
        /**
         * id : 277
         * name : อำเภอเมืองเชียงใหม่
         * parentName :
         * isLeaf : true
         * address : อำเภอเมืองเชียงใหม่ จังหวัดเชียงใหม่
         * location : {"type":"Point","coordinates":[98.95538407869883,18.790772653183357]}
         * code : domain-1-public-Amphoe Muang Chiang Mai
         * authority : 276
         * reportCount : 128
         * supportCount : 63
         * categoryCount : {"สิ่งแวดล้อม":4,"สุขภาพคน":58,"สัตว์กัด, ทำร้าย":65}
         * hotReportType : [{"total":57,"type":"สัตว์กัด"},{"total":39,"type":"อาหารเป็นพิษ"},{"total":9,"type":"ร้าน / เขียงไม่สะอาด"}]
         * canEdit : true
         */
        /**
         * total : 57
         * type : สัตว์กัด
         */

        public int id;
        public String name;
        public String parentName;
        public boolean isLeaf;
        public String address;
        public LocationEntity location;
        public String code;
        public int authority;
        public int reportCount;
        public int supportCount;

        public List<HotReportTypeEntity> hotReportType;


        /**
         * สิ่งแวดล้อม : 4
         * สุขภาพคน : 58
         * สัตว์กัด, ทำร้าย : 65
         */

        public static class HotReportTypeEntity {
            public int count;
            public String type;
        }

        public List<CategoryCountEntity> categoryCount;

        public static class CategoryCountEntity {
            public String count;
            public String code;
            public String name;
        }

        public String getHumanCategoryCount() {
            return getCategoryCount("human");
        }

        public String getAnimalCategoryCount() {
            return getCategoryCount("animal");
        }

        public String getEnvCategoryCount() {
            return getCategoryCount("environment");
        }

        public String getCategoryCount(String code) {
            String count = "";
            if (categoryCount == null) return count;
            for (int i = 0; i < categoryCount.size(); i++){
                if (categoryCount.get(i).code.equalsIgnoreCase(code)){
                    count = categoryCount.get(i).count;
                    break;
                }
            }
            return count;
        }

        public String mpoly;

        public static class PolygonEntity {
            public String type;
            public List<List<List<Double>>> coordinates;
        }


        public PolygonEntity getPolygonEntity() {
            PolygonEntity geojson = null;
            try {
                geojson = new Gson().fromJson(mpoly, PolygonEntity.class);

            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
            return geojson;
        }

    }

}
