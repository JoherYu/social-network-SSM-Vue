//package com.sharephoto.utils;
//
///**
// * @author Joher
// * @data 2019/5/21
// **/
//import java.util.Set;
//import javax.validation.ConstraintViolation;
//import javax.validation.Validation;
//import javax.validation.Validator;
//import org.hibernate.validator.HibernateValidator;
//
//import com.jy.enums.BizExceptionEnum;
//import com.jy.exception.PCBizException;
///**
// * 接口入参数据校验工具类.<br/>
// * 使用hibernate-validator进行校验.<br/>
// *
// * @Null   被注释的元素必须为 null
// * @NotNull    被注释的元素必须不为 null
// * @AssertTrue     被注释的元素必须为 true
// * @AssertFalse    被注释的元素必须为 false
// * @Min(value)     被注释的元素必须是一个数字，其值必须大于等于指定的最小值
// * @Max(value)     被注释的元素必须是一个数字，其值必须小于等于指定的最大值
// * @DecimalMin(value)  被注释的元素必须是一个数字，其值必须大于等于指定的最小值
// * @DecimalMax(value)  被注释的元素必须是一个数字，其值必须小于等于指定的最大值
// * @Size(max=, min=)   被注释的元素的大小必须在指定的范围内
// * @Digits (integer, fraction)     被注释的元素必须是一个数字，其值必须在可接受的范围内
// * @Past   被注释的元素必须是一个过去的日期
// * @Future     被注释的元素必须是一个将来的日期
// * @Pattern(regex=,flag=)  被注释的元素必须符合指定的正则表达式
// * Hibernate Validator 附加的 constraint
// * @NotBlank(message =)   验证字符串非null，且长度必须大于0
// * @Email  被注释的元素必须是电子邮箱地址
// * @Length(min=,max=)  被注释的字符串的大小必须在指定的范围内
// * @NotEmpty   被注释的字符串的必须非空
// * @Range(min=,max=,message=)  被注释的元素必须在合适的范围内
// *
// */
//public class ValidationUtils {
//
//    /**
//     * 使用hibernate的注解来进行验证
//     */
//    private static Validator validator = Validation
//            .byProvider(HibernateValidator.class).configure().failFast(true).buildValidatorFactory().getValidator();
//
//    /**
//     * 功能描述: <br>
//     * 〈注解验证参数〉
//     *
//     * @param obj
//     */
//    public static <T> void validate(T obj) {
//        Set<ConstraintViolation<T>> constraintViolations = validator.validate(obj);
//        // 抛出检验异常
//        if (constraintViolations.size() > 0) {
//            PCBizException e = new PCBizException(BizExceptionEnum.REQUEST_PARAM_ERROR.getIndex(), String.format(constraintViolations.iterator().next().getMessage()));
//            e.setInfo(constraintViolations.iterator().next().getPropertyPath()); // 字段校验未通过的字段名称
//            throw e;
//        }
//    }
//}
