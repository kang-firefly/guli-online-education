/**
 * 出参对象（Response VO）。
 * <p>约定：
 * <ul>
 *   <li>只放"要给前端看"的数据，隐藏 isDeleted、password 等内部字段；</li>
 *   <li>需要拼接的展示字段（讲师姓名、分类名、章节下的小节列表）放这里，不放 entity；</li>
 *   <li>由 ServiceImpl 用 {@code BeanUtils.copyProperties} 从 entity 映射；</li>
 *   <li>命名 {@code XxxVO}，聚合结构可用 {@code XxxDetailVO} / {@code XxxTreeVO}。</li>
 * </ul>
 */
package com.guli.vo;
