package cn.longmaster.ihuvc.core.entity;

import java.io.Serializable;

import cn.longmaster.doctorlibrary.utils.json.JsonField;

/**
 * Created by WangHaiKun on 2017/5/9.
 */

public class MaterialTypeInfo implements Serializable {
    @JsonField("material_type_name")
    private int mMaterialTypeName;//材料类型名称
    @JsonField("material_type_id")
    private int mMaterialTypeId;//材料了类型ID
    @JsonField("material_num")
    private int mMaterialNum;//当前材料类型下的图片数量

    public int getmMaterialTypeName() {
        return mMaterialTypeName;
    }

    public void setmMaterialTypeName(int mMaterialTypeName) {
        this.mMaterialTypeName = mMaterialTypeName;
    }

    public int getmMaterialTypeId() {
        return mMaterialTypeId;
    }

    public void setmMaterialTypeId(int mMaterialTypeId) {
        this.mMaterialTypeId = mMaterialTypeId;
    }

    public int getmMaterialNum() {
        return mMaterialNum;
    }

    public void setmMaterialNum(int mMaterialNum) {
        this.mMaterialNum = mMaterialNum;
    }

    @Override
    public String toString() {
        return "MaterialTypeInfo{" +
                "mMaterialTypeName=" + mMaterialTypeName +
                ", mMaterialTypeId=" + mMaterialTypeId +
                ", mMaterialNum=" + mMaterialNum +
                '}';
    }
}
