package com.brian.express6v.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by suboan on 2018/2/8.
 *
 */

public class PrintBean implements Parcelable {
    private String fhr;//发货人
    private String fhrdh;//发货人电话
    private String shr;//收货人
    private String shrdh;//收货人电话
    private String shrdz;//收货人地址
    private String tydh;//托运单号
    private String shwd;//收货网点
    private String mdwd;//目的网点
    private String hwmc;//货物名称
    private String jshj;//件数合计
    private String hj;//运费合计
    private String fkfs;//付款方式
    private String tyfs;//托运方式
    private String company;//公司
    private String remark;//备注
    private String tyrq;//托运日期

    public String getFhr() {
        return fhr;
    }

    public void setFhr(String fhr) {
        this.fhr = fhr;
    }

    public String getFhrdh() {
        return fhrdh;
    }

    public void setFhrdh(String fhrdh) {
        this.fhrdh = fhrdh;
    }

    public String getShr() {
        return shr;
    }

    public void setShr(String shr) {
        this.shr = shr;
    }

    public String getShrdh() {
        return shrdh;
    }

    public void setShrdh(String shrdh) {
        this.shrdh = shrdh;
    }

    public String getShrdz() {
        return shrdz;
    }

    public void setShrdz(String shrdz) {
        this.shrdz = shrdz;
    }

    public String getTydh() {
        return tydh;
    }

    public void setTydh(String tydh) {
        this.tydh = tydh;
    }

    public String getShwd() {
        return shwd;
    }

    public void setShwd(String shwd) {
        this.shwd = shwd;
    }

    public String getMdwd() {
        return mdwd;
    }

    public void setMdwd(String mdwd) {
        this.mdwd = mdwd;
    }

    public String getHwmc() {
        return hwmc;
    }

    public void setHwmc(String hwmc) {
        this.hwmc = hwmc;
    }

    public String getJshj() {
        return jshj;
    }

    public void setJshj(String jshj) {
        this.jshj = jshj;
    }

    public String getHj() {
        return hj;
    }

    public void setHj(String hj) {
        this.hj = hj;
    }

    public String getFkfs() {
        return fkfs;
    }

    public void setFkfs(String fkfs) {
        this.fkfs = fkfs;
    }

    public String getTyfs() {
        return tyfs;
    }

    public void setTyfs(String tyfs) {
        this.tyfs = tyfs;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTyrq() {
        return tyrq;
    }

    public void setTyrq(String tyrq) {
        this.tyrq = tyrq;
    }

    /**
     * 序列化实体类
     */
    public static final Parcelable.Creator<PrintBean> CREATOR = new Creator<PrintBean>() {
        public PrintBean createFromParcel(Parcel source) {
            PrintBean printBean = new PrintBean();
            printBean.fhr = source.readString();
            printBean.fhrdh = source.readString();
            printBean.shr = source.readString();
            printBean.shrdh = source.readString();
            printBean.shrdz = source.readString();
            printBean.tydh = source.readString();
            printBean.shwd = source.readString();
            printBean.mdwd = source.readString();
            printBean.hwmc = source.readString();
            printBean.jshj = source.readString();
            printBean.hj = source.readString();
            printBean.fkfs = source.readString();
            printBean.tyfs = source.readString();
            printBean.company = source.readString();
            printBean.remark = source.readString();
            printBean.tyrq = source.readString();
            return printBean;
        }

        public PrintBean[] newArray(int size) {
            return new PrintBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * 将实体类数据写入Parcel
     */
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(fhr);
        parcel.writeString(fhrdh);
        parcel.writeString(shr);
        parcel.writeString(shrdh);
        parcel.writeString(shrdz);
        parcel.writeString(tydh);
        parcel.writeString(shwd);
        parcel.writeString(mdwd);
        parcel.writeString(hwmc);
        parcel.writeString(jshj);
        parcel.writeString(hj);
        parcel.writeString(fkfs);
        parcel.writeString(tyfs);
        parcel.writeString(company);
        parcel.writeString(remark);
        parcel.writeString(tyrq);
    }

    @Override
    public String toString() {
        return "fhr='" + fhr + '\'' + "\n" +
                "fhrdh='" + fhrdh + '\'' + "\n" +
                "shr='" + shr + '\'' + "\n" +
                "shrdh='" + shrdh + '\'' + "\n" +
                "shrdz='" + shrdz + '\'' + "\n" +
                "tydh='" + tydh + '\'' + "\n" +
                "shwd='" + shwd + '\'' + "\n" +
                "mdwd='" + mdwd + '\'' + "\n" +
                "hwmc='" + hwmc + '\'' + "\n" +
                "jshj='" + jshj + '\'' + "\n" +
                "hj='" + hj + '\'' + "\n" +
                "fkfs='" + fkfs + '\'' + "\n" +
                "tyfs='" + tyfs + '\'' + "\n" +
                "company='" + company + '\'' + "\n" +
                "remark='" + remark + '\'' + "\n" +
                "tyrq='" + tyrq + '\'';
    }
}
