/**
 * 入参对象（Request DTO）。
 * <p>约定：
 * <ul>
 *   <li>只放"从前端进来"的数据，字段按用例需要裁剪，不照抄 entity；</li>
 *   <li>校验注解（{@code @NotBlank/@NotNull/@Size...}）写在这里，Controller 用 {@code @Valid} 触发；</li>
 *   <li>命名 {@code XxxQueryDTO}（查询） / {@code XxxSaveDTO}（新增修改） / {@code XxxLoginDTO}（认证）。</li>
 * </ul>
 */
package com.guli.dto;
