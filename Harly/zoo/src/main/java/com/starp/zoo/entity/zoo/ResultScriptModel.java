package com.starp.zoo.entity.zoo;

/**
 * @author covey
 */
public class ResultScriptModel {

    private String identification;

    private String name;

    private String script;

    public ResultScriptModel(String identification, String name, String script) {
        this.identification = identification;
        this.name = name;
        this.script = script;
    }

    public ResultScriptModel() {
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
}
