// ElderlyCourseTools.java
package com.main.chatai.tools;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.main.chatai.entity.po.ElderlyCourse;
import com.main.chatai.entity.po.ElderlyCourseReservation;
import com.main.chatai.entity.po.ElderlySchool;
import com.main.chatai.entity.query.ElderlyCourseQuery;
import com.main.chatai.service.IElderlyCourseReservationService;
import com.main.chatai.service.IElderlyCourseService;
import com.main.chatai.service.IElderlySchoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ElderlyCourseTools {

    private final IElderlyCourseService elderlyCourseService;

    private final IElderlySchoolService elderlySchoolService;

    private final IElderlyCourseReservationService elderlyReservationService;

    @Tool(description = "根据条件查询老年大学课程")
    public List<ElderlyCourse> queryElderlyCourse(@ToolParam(description = "查询的条件", required = false) ElderlyCourseQuery query) {
        if(query == null){
            return List.of();
        }
        QueryChainWrapper<ElderlyCourse> wrapper = elderlyCourseService.query()
                .eq(query.getType() != null, "type", query.getType())
                .le(query.getAge() != null, "age", query.getAge());

        if(query.getSorts() != null && !query.getSorts().isEmpty()){
            for (ElderlyCourseQuery.Sort sort : query.getSorts()) {
                wrapper.orderBy(true, sort.getAsc(), sort.getField());
            }
        }
        return wrapper.list();
    }

    @Tool(description = "查询所有老年大学教学点")
    public List<ElderlySchool> queryElderlySchool() {
        return elderlySchoolService.list();
    }

    @Tool(description = "生成老年大学活动预约单,返回预约单号")
    public Integer createElderlyCourseReservation(@ToolParam(description = "预约课程") String course,
                                          @ToolParam(description = "预约教学点") String school,
                                          @ToolParam(description = "老人姓名") String elderlyName,
                                          @ToolParam(description = "联系方式") String contactInfo,
                                          @ToolParam(description = "紧急联系人", required = false) String emergencyContact,
                                          @ToolParam(description = "备注", required = false) String remark) {
        ElderlyCourseReservation reservation = new ElderlyCourseReservation();
        reservation.setCourse(course);
        reservation.setSchool(school);
        reservation.setElderlyName(elderlyName);
        reservation.setContactInfo(contactInfo);
        reservation.setEmergencyContact(emergencyContact);
        reservation.setRemark(remark);
        elderlyReservationService.save(reservation);

        return reservation.getId();
    }
}