package org.tlh.profile.vo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.tlh.profile.dto.ModelTagDto;
import org.tlh.profile.enums.ModelTaskState;

import java.io.IOException;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-03-24
 */
@Data
public class BasicTagListVo extends ModelTagDto {

    @JsonSerialize(using = TaskStateSerialize.class)
    private Integer state;

    private String scheduleRule;

    public static final class TaskStateSerialize extends JsonSerializer {

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value != null) {
                ModelTaskState convert = ModelTaskState.convert((int) value);
                //写出对象
                gen.writeStartObject();
                gen.writeNumberField("state",convert.getState());
                gen.writeStringField("dec",convert.getDesc());
                gen.writeEndObject();
            } else {
                gen.writeNull();
            }
        }
    }

}
