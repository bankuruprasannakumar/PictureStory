package com.picturestory.service.response;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by bankuru on 21/8/15.
 */
@XmlRootElement
public class WebResponseData {

    private Diagnostics diagnostics;
    private Data data;

    public Diagnostics getDiagnostics() {
        return diagnostics;
    }

    public void setDiagnostics(Diagnostics diagnostics) {
        this.diagnostics = diagnostics;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public WebResponseData(){
        this.diagnostics = new Diagnostics();
        this.data = new Data();
    }

    public class Diagnostics {
        private String errorMessage;
        private int errorCode;

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public int getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(int errorCode) {
            this.errorCode = errorCode;
        }
    }
    public class Data {
        private boolean success;
        private String data;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }

}
