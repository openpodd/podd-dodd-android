package org.cm.podd.urban.report.data;

/**
 * Created by nat on 10/28/15 AD.
 */
public class FormDataEntity {
    public String remark;
    public String address;

    public static class HumanPoisonousFormDataEntity extends FormDataEntity {
        public String symptom;
        public String otherSymptom;
        public int sickCount;
        public String foodSuspect;
    }

    public static class HumanFoodDirtyFormDataEntity extends FormDataEntity {

    }

    public static class HumanFoodContaminatedFormDataEntity extends FormDataEntity {
        public String suspect;
    }

    public static class HumanFoodTooCheapMeatFormDataEntity extends FormDataEntity {
        public String type;
        public String price;
    }

    public static class HumanFoodRepeatedUsedOilFormDataEntity extends FormDataEntity {
        public String foodSuspect;
    }

    public static class AnimalBittenFormDataEntity extends FormDataEntity {
        public String animalType;
        public String animalStatus;
        public String symptom;
    }

    public static class AnimalSickDeadFormDataEntity extends FormDataEntity {
        public String symptom;
        public String animalType;
        public int sickCount;
        public int deathCount;
    }


}



