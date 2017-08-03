package com.etcxc.android.bean;

import java.util.List;

/**
 * 网点实体类
 * Created by caoyu on 2017/8/3.
 */

public class Networkstore {

    /**
     * code : s_ok
     * var : [{"id":1,"area":"长沙市","netstores_name":"迅畅科技-总部","netstores_address":"长沙市天心区芙蓉南路暮云弘高车世界（长沙生态动物园斜对面）","person_charge":"朱秀宏","phone":"0731-89580065","remark":""},{"id":2,"area":"长沙市","netstores_name":"华隆河西4S店","netstores_address":"高新开发区嘉运路10号","person_charge":"姜慧玲","phone":"15874224202","remark":""},{"id":3,"area":"长沙市","netstores_name":"华隆四方坪4S店","netstores_address":"长沙市开福区三一大道311号","person_charge":"李金辉","phone":"13142025082","remark":""},{"id":4,"area":"长沙市","netstores_name":"华达福特4S店","netstores_address":"长沙市中南汽车大世界F08栋","person_charge":"张琼","phone":"15274918454","remark":""},{"id":5,"area":"长沙市","netstores_name":"星沙新城吉普4S店","netstores_address":"长沙市中南汽车世界金茂西路H-02Jeep4s店","person_charge":"王立满","phone":"13397411269","remark":""},{"id":6,"area":"长沙市","netstores_name":"车尚汽车服务会所","netstores_address":"长沙县东四路与盼盼交汇处旭辉华庭5-104门面","person_charge":"罗笑","phone":"15367882998","remark":""},{"id":7,"area":"长沙市","netstores_name":"新达吉普店","netstores_address":"天心区黑石铺镇梓枫塘雀园路6S街区（省政府往南2公里）","person_charge":"罗银花","phone":"13873191626","remark":""},{"id":8,"area":"长沙市","netstores_name":"卡酷汽车生活馆","netstores_address":"长沙市天心区书香路石人村8-10号门面 ","person_charge":"刘英","phone":"18684899936","remark":""},{"id":9,"area":"长沙市","netstores_name":"易通达汽车社区","netstores_address":"长沙市芙蓉区沙湾路1688号（芙蓉公寓南门三岔路口左行200米）","person_charge":"吴波","phone":"18874194065","remark":"发卡及充值点"},{"id":10,"area":"长沙市","netstores_name":"金尔泰汽车维修","netstores_address":"湘江世纪城金泰路江临天下小区正对面","person_charge":"黄金辉","phone":"18627565020","remark":""},{"id":11,"area":"长沙市","netstores_name":"宁乡中拓瑞宁","netstores_address":"宁乡白马大道二环南路","person_charge":"文叶","phone":"13755166692","remark":""},{"id":12,"area":"ETC客服中心","netstores_name":"咸嘉湖客服中心","netstores_address":"长沙市岳麓区咸嘉湖西路与谷丰南路交叉口西北100米","person_charge":"梁志彦","phone":"13974853879","remark":""},{"id":13,"area":"ETC客服中心","netstores_name":"新中路客服中心","netstores_address":"长沙市雨花区南二环桔园立交桥东赛格新中路营业部","person_charge":"翁秦","phone":"15074805096","remark":""},{"id":14,"area":"ETC客服中心","netstores_name":"月湖客服中心","netstores_address":"湖南省长沙市开福区万家丽北路一段899号(月湖公园东月湖兰亭旁)工商银行内","person_charge":"周新玉、 熊元元 ","phone":"18773142180、18821953309","remark":""},{"id":15,"area":"ETC客服中心","netstores_name":"恒广国际物流园","netstores_address":"长沙县安沙镇物流大道恒广国际物流园B07栋101-106号，125-130号门面","person_charge":"刘海燕","phone":"0731-84089212","remark":"发卡及充值点"},{"id":16,"area":"ETC客服中心","netstores_name":"郎梨工业园","netstores_address":"湖南省长沙县榔梨镇榔梨工业园星湖路19号浦星物流园内","person_charge":"吴玉贞","phone":"0731-86201188","remark":"发卡及充值点"},{"id":17,"area":"ETC客服中心","netstores_name":"怀化赛格","netstores_address":"怀化市红星南路西南汽车城二楼赛格车圣GPS营业部","person_charge":"彭莉","phone":"13349650999","remark":"发卡及充值点"},{"id":18,"area":"ETC客服中心","netstores_name":"娄底长安马自达","netstores_address":"娄底市湘阳街1956号（棉纺厂往大埠桥方向800米）","person_charge":"张晋峰","phone":"15897380778","remark":""},{"id":20,"area":"地市","netstores_name":"株洲马自达","netstores_address":"株洲市荷塘区红旗中路55号","person_charge":"王小红","phone":"18273317939","remark":""},{"id":21,"area":"地市","netstores_name":"岳阳赛格","netstores_address":"岳阳楼区青年东路1226号赛格车圣汽车导航有限公司","person_charge":"易峥嵘","phone":"13873052333","remark":"发卡及充值点"},{"id":23,"area":"地市","netstores_name":"益阳长安马自达","netstores_address":"朝阳市场东方维也纳上S308省道往湘阴方向1000米","person_charge":"陈志辉","phone":"17752701412","remark":""},{"id":24,"area":"地市","netstores_name":"邵阳长安马自达","netstores_address":"邵阳市邵阳大道与大兴路交叉口往北200米","person_charge":"杨宗航","phone":"15211903473","remark":""},{"id":26,"area":"地市","netstores_name":"郴州长安马自达","netstores_address":"郴州市开发区南岭大道（天龙站往南200米）","person_charge":"黄进","phone":"18673725442","remark":""},{"id":27,"area":"地市","netstores_name":"吉首长安马自达","netstores_address":"湖南省吉首市大田湾广通汽车城9、10号（长安马自达店）","person_charge":"方艳青","phone":"13135098081","remark":""},{"id":28,"area":"地市","netstores_name":"张家界长安马自达","netstores_address":"张家界市永定区宝塔岗欣业家园四号门面","person_charge":"姚辉明","phone":"15116429640","remark":""},{"id":29,"area":"地市","netstores_name":"邵东长安马自达","netstores_address":"邵东县衡宝路与宋家塘交叉口铁路桥旁","person_charge":"何鹏","phone":"18273971100","remark":"发卡及充值点"}]
     */

    private String code;
    private List<VarBean> var;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<VarBean> getVar() {
        return var;
    }

    public void setVar(List<VarBean> var) {
        this.var = var;
    }

    public static class VarBean {
        /**
         * id : 1
         * area : 长沙市
         * netstores_name : 迅畅科技-总部
         * netstores_address : 长沙市天心区芙蓉南路暮云弘高车世界（长沙生态动物园斜对面）
         * person_charge : 朱秀宏
         * phone : 0731-89580065
         * remark :
         */

        private int id;
        private String area;
        private String netstores_name;
        private String netstores_address;
        private String person_charge;
        private String phone;
        private String remark;
        private Double distance;

        public Double getDistance() {
            return distance;
        }

        public void setDistance(Double distance) {
            this.distance = distance;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getNetstores_name() {
            return netstores_name;
        }

        public void setNetstores_name(String netstores_name) {
            this.netstores_name = netstores_name;
        }

        public String getNetstores_address() {
            return netstores_address;
        }

        public void setNetstores_address(String netstores_address) {
            this.netstores_address = netstores_address;
        }

        public String getPerson_charge() {
            return person_charge;
        }

        public void setPerson_charge(String person_charge) {
            this.person_charge = person_charge;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        @Override
        public String toString() {
            return "VarBean{" +
                    "id=" + id +
                    ", area='" + area + '\'' +
                    ", netstores_name='" + netstores_name + '\'' +
                    ", netstores_address='" + netstores_address + '\'' +
                    ", person_charge='" + person_charge + '\'' +
                    ", phone='" + phone + '\'' +
                    ", remark='" + remark + '\'' +
                    ", distance=" + distance +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Networkstore{" +
                "code='" + code + '\'' +
                ", var=" + var +
                '}';
    }
}
