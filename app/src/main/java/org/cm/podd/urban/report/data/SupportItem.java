package org.cm.podd.urban.report.data;

import java.util.List;

/**
 * Created by nat on 11/3/15 AD.
 */
public class SupportItem {

    /**
     * administrationAreaAddress : อำเภอเมืองเชียงใหม่ จังหวัดเชียงใหม่
     * administrationAreaId : 277
     * commentCount : 0
     * createdAt : 2015-11-03T11:09:17.052779+00:00
     * createdBy : Thanyawan Mingsong
     * createdById : 1257
     * createdByName : Thanyawan Mingsong
     * createdByThumbnailUrl : https://fbcdn-profile-a.akamaihd.net/hprofile-ak-xal1/v/t1.0-1/p200x200/12074491_10153567264577435_2076035115835140580_n.jpg?oh=6b47e99573d154ca974ed903e45c1777&oe=56C9A189&__gda__=1455819579_20ccffe725d7166d004378503e4d36db
     * date : 2015-10-28T08:00:01+00:00
     * formDataExplanation : !YO
     * guid : 7904c6e6c4ef1af4-1446548943934
     * id : 80534
     * images : [{"guid":"7904c6e6c4ef1af4-1446548943934","imageUrl":"http://thaispendingwatch.s3.amazonaws.com/JPEG_20151103_180841_-1457933142.jpg","location":{},"report":80534,"thumbnailUrl":"http://thaispendingwatch.s3.amazonaws.com/JPEG_20151103_180841_-1457933142.jpg"},{"guid":"7904c6e6c4ef1af4-1446548943934","imageUrl":"http://thaispendingwatch.s3.amazonaws.com/JPEG_20151027_163001_672699270.jpg","location":{},"report":80534,"thumbnailUrl":"http://thaispendingwatch.s3.amazonaws.com/JPEG_20151027_163001_672699270.jpg"},{"guid":"7904c6e6c4ef1af4-1446548943934","imageUrl":"http://thaispendingwatch.s3.amazonaws.com/JPEG_20151027_164633_-1222856085.jpg","location":{},"report":80534,"thumbnailUrl":"http://thaispendingwatch.s3.amazonaws.com/JPEG_20151027_164633_-1222856085.jpg"},{"guid":"7904c6e6c4ef1af4-1446548943934","imageUrl":"http://thaispendingwatch.s3.amazonaws.com/JPEG_20151027_175738_672699270.jpg","location":{},"report":80534,"thumbnailUrl":"http://thaispendingwatch.s3.amazonaws.com/JPEG_20151027_175738_672699270.jpg"},{"guid":"7904c6e6c4ef1af4-1446548943934","imageUrl":"http://thaispendingwatch.s3.amazonaws.com/JPEG_20151022_142653_672699270.jpg","location":{},"report":80534,"thumbnailUrl":"http://thaispendingwatch.s3.amazonaws.com/JPEG_20151022_142653_672699270.jpg"},{"guid":"7904c6e6c4ef1af4-1446548943934","imageUrl":"http://thaispendingwatch.s3.amazonaws.com/JPEG_20151022_142653_672699270.jpg","location":{},"report":80534,"thumbnailUrl":"http://thaispendingwatch.s3.amazonaws.com/JPEG_20151022_142653_672699270.jpg"},{"guid":"7904c6e6c4ef1af4-1446548943934","imageUrl":"http://thaispendingwatch.s3.amazonaws.com/JPEG_20151006_203914_-338745386.jpg","location":{},"report":80534,"thumbnailUrl":"http://thaispendingwatch.s3.amazonaws.com/JPEG_20151006_203914_-338745386.jpg"},{"guid":"7904c6e6c4ef1af4-1446548943934","imageUrl":"http://thaispendingwatch.s3.amazonaws.com/JPEG_20151027_163500_672699270.jpg","location":{},"report":80534,"thumbnailUrl":"http://thaispendingwatch.s3.amazonaws.com/JPEG_20151027_163500_672699270.jpg"},{"guid":"7904c6e6c4ef1af4-1446548943934","imageUrl":"http://thaispendingwatch.s3.amazonaws.com/JPEG_20151027_163001_672699270.jpg","location":{},"report":80534,"thumbnailUrl":"http://thaispendingwatch.s3.amazonaws.com/JPEG_20151027_163001_672699270.jpg"}]
     * incidentDate : 2015-10-28
     * isPublic : true
     * likeCount : 1
     * likeId : 74
     * meTooCount : 1
     * meTooId : 75
     * negative : true
     * remark :
     * renderedOriginalFormData : !YO
     * reportId : 1234
     * reportLocation : {"coordinates":[98.979821,18.705088],"type":"Point"}
     * reportTypeCategoryCode : human
     * reportTypeId : 30
     * reportTypeName : ร้าน / เขียงไม่สะอาด
     * state : 0
     * stateCode : report
     * stateId : 48
     * testFlag : false
     */

    public String administrationAreaAddress;
    public int administrationAreaId;
    public int commentCount;
    public String createdAt;
    public String createdBy;
    public int createdById;
    public String createdByName;
    public String createdByThumbnailUrl;
    public String date;
    public String formDataExplanation;
    public String guid;
    public int id;
    public String incidentDate;
    public boolean isPublic;
    public int likeCount;
    public int likeId;
    public int meTooCount;
    public int meTooId;
    public boolean negative;
    public String remark;
    public String renderedOriginalFormData;
    public int reportId;
    /**
     * coordinates : [98.979821,18.705088]
     * type : Point
     */

    private ReportLocationEntity reportLocation;
    private String reportTypeCategoryCode;
    private int reportTypeId;
    private String reportTypeName;
    private int state;
    private String stateCode;
    private int stateId;
    private boolean testFlag;
    /**
     * guid : 7904c6e6c4ef1af4-1446548943934
     * imageUrl : http://thaispendingwatch.s3.amazonaws.com/JPEG_20151103_180841_-1457933142.jpg
     * location : {}
     * report : 80534
     * thumbnailUrl : http://thaispendingwatch.s3.amazonaws.com/JPEG_20151103_180841_-1457933142.jpg
     */

    private List<ImagesEntity> images;

    public static class ReportLocationEntity {
        public String type;
        public List<Double> coordinates;


    }

    public static class ImagesEntity {
        public String guid;
        public String imageUrl;
        public LocationEntity location;
        public int report;
        public String thumbnailUrl;

    }
}
