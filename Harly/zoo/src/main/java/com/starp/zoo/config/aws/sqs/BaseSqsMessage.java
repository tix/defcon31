package com.starp.zoo.config.aws.sqs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author starp
 */
@Data
public class BaseSqsMessage implements Serializable {

    Integer[] types;

    String msgName;

    Object msgBody;

    public BaseSqsMessage() {
    }

    @JsonCreator
    public BaseSqsMessage(@JsonProperty("types") Integer[] types, @JsonProperty("msgName") String msgName, @JsonProperty("msgBody") Object msgBody){
        if(types != null) {
            List<Integer> list = new ArrayList<>();
            list.addAll(Arrays.asList(types));
            this.types = list.toArray(new Integer[list.size()]);
        }else {
            this.types = new Integer[0];
        }
        this.msgName = msgName;
        this.msgBody = msgBody;
    }

    public Integer[] getTypes() {
        if(types != null) {
            List<Integer> list = new ArrayList<>();
            list.addAll(Arrays.asList(types));
            return list.toArray(new Integer[list.size()]);
        }
        return new Integer[0];
    }

    public void setTypes(Integer[] types) {
        if(types != null) {
            List<Integer> list = new ArrayList<>();
            list.addAll(Arrays.asList(types));
            this.types = list.toArray(new Integer[list.size()]);
        }else {
            this.types = new Integer[0];
        }
    }
}
