package org.cm.podd.urban.report.data;

/**
 * Created by nat on 11/16/15 AD.
 */
public class PostModel {

    public static class ReportImagesModel {
        /**
         * reportGuid : a20021
         * imageUrl : https://s3-ap-southeast-1.amazonaws.com/podd/c312b30e-a8a3-4bc8-90f7-eba851f5f4ed
         * thumbnailUrl : https://s3-ap-southeast-1.amazonaws.com/podd/c312b30e-a8a3-4bc8-90f7-eba851f5f4ed-thumbnail
         */
        public String reportGuid;
        public String guid;
        public String imageUrl;
        public String thumbnailUrl;
    }

    public static class PostReportModel {

        /**
         * incidentDate : 2015-02-21
         * date : 2015-02-21T17:00:01+0700
         * reportTypeId : 2
         * reportGuid : a20021
         * reportId : 20020
         * administrationAreaId : 3
         * remark : Andrew got it well
         * formData : {"animalType":"หมา","sickCount":2,"disease":""}
         * reportLocation : {"latitude":13.808277,"longitude":100.752206}
         */

        public String incidentDate;
        public String date;
        public int reportId;
        public String guid;
        public String reportTypeCode;
        public String remark;
        public FormDataEntity formData;
        public ReportLocationEntity reportLocation;
        public boolean isAnonymous;

        public static class ReportLocationEntity {
            /**
             * latitude : 13.808277
             * longitude : 100.752206
             */

            public double latitude;
            public double longitude;
        }
    }

    public static class SupportPostModel {
        public int reportId;
        public String message;
        public boolean isLike;
        public boolean isMeToo;
    }

}
