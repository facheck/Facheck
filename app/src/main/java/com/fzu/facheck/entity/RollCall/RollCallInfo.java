package com.fzu.facheck.entity.RollCall;

import java.util.List;

public class RollCallInfo {
    /**
     * code : 0900
     * classInfo : {"phoneNumber":"33333333","joinedClassData":[{"joinedClassId":"00000001","joinedClassName":"高等数学","joinedClassTime":"星期一 上午8:20-10:00","managerName":"小红","signable":false,"attendanceRate":"100.0%","state":"0"}],"managedClassData":[{"managedClassId":"00000002","managedClassName":"线性代数","managedClassTime":"星期二 上午8:20-10:00","ableRollCall":true},{"managedClassId":"00000003","managedClassName":"概率论与数理统计","managedClassTime":"星期五 上午8:20-10:00","ableRollCall":true}]}
     */

    private String code;
    private ClassInfoBean classInfo;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ClassInfoBean getClassInfo() {
        return classInfo;
    }

    public void setClassInfo(ClassInfoBean classInfo) {
        this.classInfo = classInfo;
    }

    public static class ClassInfoBean {
        /**
         * phoneNumber : 33333333
         * joinedClassData : [{"joinedClassId":"00000001","joinedClassName":"高等数学","joinedClassTime":"星期一 上午8:20-10:00","managerName":"小红","signable":false,"attendanceRate":"100.0%","state":"0"}]
         * managedClassData : [{"managedClassId":"00000002","managedClassName":"线性代数","managedClassTime":"星期二 上午8:20-10:00","ableRollCall":true},{"managedClassId":"00000003","managedClassName":"概率论与数理统计","managedClassTime":"星期五 上午8:20-10:00","ableRollCall":true}]
         */

        private String phoneNumber;
        private List<JoinedClassDataBean> joinedClassData;
        private List<ManagedClassDataBean> managedClassData;

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public List<JoinedClassDataBean> getJoinedClassData() {
            return joinedClassData;
        }

        public void setJoinedClassData(List<JoinedClassDataBean> joinedClassData) {
            this.joinedClassData = joinedClassData;
        }

        public List<ManagedClassDataBean> getManagedClassData() {
            return managedClassData;
        }

        public void setManagedClassData(List<ManagedClassDataBean> managedClassData) {
            this.managedClassData = managedClassData;
        }

        public static class JoinedClassDataBean {
            /**
             * joinedClassId : 00000001
             * joinedClassName : 高等数学
             * joinedClassTime : 星期一 上午8:20-10:00
             * managerName : 小红
             * signable : false
             * attendanceRate : 100.0%
             * state : 0
             */

            private String joinedClassId;
            private String joinedClassName;
            private String joinedClassTime;
            private String managerName;
            private boolean signable;
            private String attendanceRate;
            private String state;

            public String getJoinedClassId() {
                return joinedClassId;
            }

            public void setJoinedClassId(String joinedClassId) {
                this.joinedClassId = joinedClassId;
            }

            public String getJoinedClassName() {
                return joinedClassName;
            }

            public void setJoinedClassName(String joinedClassName) {
                this.joinedClassName = joinedClassName;
            }

            public String getJoinedClassTime() {
                return joinedClassTime;
            }

            public void setJoinedClassTime(String joinedClassTime) {
                this.joinedClassTime = joinedClassTime;
            }

            public String getManagerName() {
                return managerName;
            }

            public void setManagerName(String managerName) {
                this.managerName = managerName;
            }

            public boolean isSignable() {
                return signable;
            }

            public void setSignable(boolean signable) {
                this.signable = signable;
            }

            public String getAttendanceRate() {
                return attendanceRate;
            }

            public void setAttendanceRate(String attendanceRate) {
                this.attendanceRate = attendanceRate;
            }

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }
        }

        public static class ManagedClassDataBean {
            /**
             * managedClassId : 00000002
             * managedClassName : 线性代数
             * managedClassTime : 星期二 上午8:20-10:00
             * ableRollCall : "0"：可以点名，"1"正在点名，"2"点名结束
             */

            private String managedClassId;
            private String managedClassName;
            private String managedClassTime;
            private String ableRollCall;

            public String getManagedClassId() {
                return managedClassId;
            }

            public void setManagedClassId(String managedClassId) {
                this.managedClassId = managedClassId;
            }

            public String getManagedClassName() {
                return managedClassName;
            }

            public void setManagedClassName(String managedClassName) {
                this.managedClassName = managedClassName;
            }

            public String getManagedClassTime() {
                return managedClassTime;
            }

            public void setManagedClassTime(String managedClassTime) {
                this.managedClassTime = managedClassTime;
            }

            public String isAbleRollCall() {
                return ableRollCall;
            }

            public void setAbleRollCall(String ableRollCall) {
                this.ableRollCall = ableRollCall;
            }
        }
    }





}
