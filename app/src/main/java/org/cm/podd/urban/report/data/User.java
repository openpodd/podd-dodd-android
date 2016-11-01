package org.cm.podd.urban.report.data;

import org.cm.podd.urban.report.helper.DateUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by nat on 10/14/15 AD.
 */
public class User {

    /**
     * id : 1
     * username : admin
     * firstName :
     * lastName :
     * status :
     * contact :
     * avatarUrl : https://podd.s3.amazonaws.com/0912bd24-1849-4e89-9140-e3468e49a82e
     * thumbnailAvatarUrl : https://podd.s3.amazonaws.com/6ae24bd3-9f93-47eb-aca4-c326acf265f9
     * isStaff : true
     * isSuperuser : true
     * isAnonymous : false
     */
    public static class Model {
        public int id;
        public String username;
        public String name;
        public String firstName;
        public String lastName;
        public String status;
        public String contact;
        public String avatarUrl;
        public String thumbnailAvatarUrl;
        public boolean isStaff;
        public boolean isSuperuser;
        public boolean isAnonymous;
        public boolean isPublic;
        public String token;
        public String email;
        public String displayPassword;
        public List<String> permissions;

        public String telephone;
        public String reportCount;
        public String supportCount;

        public String dateJoined;


        public static final String TAG = Model.class.getSimpleName();

        public String getAvatarThumbnail() {
            if ("".equalsIgnoreCase(this.thumbnailAvatarUrl) == false) {
                return this.thumbnailAvatarUrl;
            } else {
                return null;
            }


        }

        public List<CategoryCountEntity> categoryCount;

        public static class CategoryCountEntity {
            public String count;
            public String code;
            public String name;
        }

        public String getAvatarImageUrl() {
            return thumbnailAvatarUrl;
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
            for (int i = 0; i < categoryCount.size(); i++) {
                if (categoryCount.get(i).code.equalsIgnoreCase(code)) {
                    count = categoryCount.get(i).count;
                    break;
                }
            }
            return count;
        }

        public String getDateJoined() {
            if (dateJoined == null)
                return "";

//            try {
            Date d = DateUtil.fromJsonDateString(dateJoined, 7);
            String out = DateUtil.convertToThaiDate(d);

//                DateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH);
//                Date date = inFormat.parse(dateJoined);
//
//                DateFormat outFormat = new SimpleDateFormat("MMMM yyyy", new Locale("th", "TH"));
            return "เริ่มรายงานเมื่อ " + out;
//            } catch (ParseException ex) {
//                return "";
//            }
        }
    }


}
