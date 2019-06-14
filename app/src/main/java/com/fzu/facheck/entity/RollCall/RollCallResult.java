package com.fzu.facheck.entity.RollCall;

import java.util.List;

public class RollCallResult {

    /**
     * code : 1000
     * recordInfo : {"recordId":"00000001","time":"2019-05-02 13:33:17.641970","alreadySignedIn":[{"authenticationId":"031602319","name":"L"},{"authenticationId":"031602335","name":"W"}],"notYetSignedIn":[{"authenticationId":"031602311","name":"Q"},{"authenticationId":"031602332","name":"H"}],"alreadyNumber":2,"notYetNumber":2,"totalNumber":4,"attendanceRate":"50.0%"}
     */

    private String code;
    private RecordInfoBean recordInfo;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public RecordInfoBean getRecordInfo() {
        return recordInfo;
    }

    public void setRecordInfo(RecordInfoBean recordInfo) {
        this.recordInfo = recordInfo;
    }

    public static class RecordInfoBean {
        /**
         * recordId : 00000001
         * time : 2019-05-02 13:33:17.641970
         * alreadySignedIn : [{"authenticationId":"031602319","name":"L"},{"authenticationId":"031602335","name":"W"}]
         * notYetSignedIn : [{"authenticationId":"031602311","name":"Q"},{"authenticationId":"031602332","name":"H"}]
         * alreadyNumber : 2
         * notYetNumber : 2
         * totalNumber : 4
         * attendanceRate : 50.0%
         */

        private String recordId;
        private String time;
        private int alreadyNumber;
        private int notYetNumber;
        private int totalNumber;
        private String attendanceRate;
        private List<AlreadySignedInBean> alreadySignedIn;
        private List<NotYetSignedInBean> notYetSignedIn;

        public String getRecordId() {
            return recordId;
        }

        public void setRecordId(String recordId) {
            this.recordId = recordId;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getAlreadyNumber() {
            return alreadyNumber;
        }

        public void setAlreadyNumber(int alreadyNumber) {
            this.alreadyNumber = alreadyNumber;
        }

        public int getNotYetNumber() {
            return notYetNumber;
        }

        public void setNotYetNumber(int notYetNumber) {
            this.notYetNumber = notYetNumber;
        }

        public int getTotalNumber() {
            return totalNumber;
        }

        public void setTotalNumber(int totalNumber) {
            this.totalNumber = totalNumber;
        }

        public String getAttendanceRate() {
            return attendanceRate;
        }

        public void setAttendanceRate(String attendanceRate) {
            this.attendanceRate = attendanceRate;
        }

        public List<AlreadySignedInBean> getAlreadySignedIn() {
            return alreadySignedIn;
        }

        public void setAlreadySignedIn(List<AlreadySignedInBean> alreadySignedIn) {
            this.alreadySignedIn = alreadySignedIn;
        }

        public List<NotYetSignedInBean> getNotYetSignedIn() {
            return notYetSignedIn;
        }

        public void setNotYetSignedIn(List<NotYetSignedInBean> notYetSignedIn) {
            this.notYetSignedIn = notYetSignedIn;
        }

        public static class AlreadySignedInBean {
            /**
             * authenticationId : 031602319
             * name : L
             */

            private String authenticationId;
            private String name;

            public String getAuthenticationId() {
                return authenticationId;
            }

            public void setAuthenticationId(String authenticationId) {
                this.authenticationId = authenticationId;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class NotYetSignedInBean {
            /**
             * authenticationId : 031602311
             * name : Q
             */

            private String authenticationId;
            private String name;

            public String getAuthenticationId() {
                return authenticationId;
            }

            public void setAuthenticationId(String authenticationId) {
                this.authenticationId = authenticationId;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}
