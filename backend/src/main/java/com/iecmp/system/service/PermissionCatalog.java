package com.iecmp.system.service;

import com.iecmp.system.vo.PermissionTreeVO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 接口 1-21 的静态权限码目录。
 *
 * <p>v1 权限码由后端登记、只读下发；16.6 再升级为可维护菜单/按钮表。
 */
public final class PermissionCatalog {

    private static final List<PermissionTreeVO> TREE;
    private static final Set<String> CODES;

    static {
        LinkedHashMap<String, String> system = new LinkedHashMap<>();
        system.put("system:user:list", "用户查询");
        system.put("system:user:query", "用户详情");
        system.put("system:user:add", "用户新增");
        system.put("system:user:edit", "用户编辑");
        system.put("system:user:reset", "重置密码");
        system.put("system:user:remove", "用户删除");
        system.put("system:role:list", "角色查询");
        system.put("system:role:add", "角色新增");
        system.put("system:role:edit", "角色编辑");
        system.put("system:role:remove", "角色删除");
        system.put("system:role:auth", "角色授权");
        system.put("system:register:list", "注册申请查询");
        system.put("system:register:audit", "注册申请审核");
        system.put("system:invite:list", "邀请码查询");
        system.put("system:invite:add", "邀请码生成");
        system.put("system:invite:remove", "邀请码作废");
        system.put("system:log:list", "操作日志查询");

        LinkedHashMap<String, String> repo = new LinkedHashMap<>();
        repo.put("repo:account:list", "仓库账户查询");
        repo.put("repo:stat:list", "仓库统计查询");
        repo.put("repo:sync:list", "同步日志查询");
        repo.put("repo:sync:exec", "手动补采");

        LinkedHashMap<String, String> budget = new LinkedHashMap<>();
        budget.put("budget:period:list", "预算周期查询");
        budget.put("budget:period:add", "预算周期新增");
        budget.put("budget:period:edit", "预算周期编辑");
        budget.put("budget:period:remove", "预算周期删除");
        budget.put("budget:period:audit", "预算周期状态流转");
        budget.put("budget:period:public", "预算公示");
        budget.put("budget:collect:exec", "发起预算征集");
        budget.put("budget:item:edit", "预算科目维护");
        budget.put("budget:apply:list", "预算申请查询");
        budget.put("budget:apply:audit", "预算申请审核");
        budget.put("budget:expense:add", "支出登记");
        budget.put("budget:expense:list", "支出查询");
        budget.put("budget:expense:remove", "支出删除");
        budget.put("budget:stat:list", "预算统计");

        LinkedHashMap<String, String> asset = new LinkedHashMap<>();
        asset.put("asset:info:list", "资产查询");
        asset.put("asset:info:query", "资产详情");
        asset.put("asset:info:add", "资产新增");
        asset.put("asset:info:edit", "资产编辑");
        asset.put("asset:info:remove", "资产删除");
        asset.put("asset:info:assign", "资产分配");
        asset.put("asset:info:export", "资产导出");
        asset.put("asset:category:edit", "资产分类维护");
        asset.put("asset:record:add", "变动登记");
        asset.put("asset:record:list", "变动流水查询");
        asset.put("asset:borrow:list", "借用申请查询");
        asset.put("asset:borrow:audit", "借用审核");
        asset.put("asset:borrow:edit", "归还登记");
        asset.put("asset:inventory:add", "发起盘点");
        asset.put("asset:inventory:list", "盘点查询");
        asset.put("asset:inventory:exec", "盘点执行");

        LinkedHashMap<String, String> seat = new LinkedHashMap<>();
        seat.put("seat:area:list", "区域查询");
        seat.put("seat:area:add", "区域新增");
        seat.put("seat:area:edit", "区域编辑");
        seat.put("seat:area:remove", "区域删除");
        seat.put("seat:seat:list", "工位查询");
        seat.put("seat:seat:add", "工位新增");
        seat.put("seat:seat:edit", "工位编辑");
        seat.put("seat:seat:remove", "工位删除");
        seat.put("seat:seat:assign", "工位分配");
        seat.put("seat:apply:list", "工位申请查询");
        seat.put("seat:apply:audit", "工位申请审核");
        seat.put("seat:swap:list", "调换申请查询");
        seat.put("seat:swap:audit", "调换申请审核");

        LinkedHashMap<String, String> honor = new LinkedHashMap<>();
        honor.put("honor:alumni:list", "学长信息查询");
        honor.put("honor:alumni:add", "学长信息新增");
        honor.put("honor:alumni:edit", "学长信息编辑");
        honor.put("honor:alumni:remove", "学长信息删除");
        honor.put("honor:alumni:audit", "学长信息审核");
        honor.put("honor:record:edit", "荣誉记录维护");

        LinkedHashMap<String, String> duty = new LinkedHashMap<>();
        duty.put("duty:plan:list", "排班计划查询");
        duty.put("duty:plan:add", "排班计划新增");
        duty.put("duty:plan:edit", "排班计划编辑");
        duty.put("duty:plan:remove", "排班计划删除");
        duty.put("duty:plan:generate", "自动排班");
        duty.put("duty:plan:publish", "排班发布");
        duty.put("duty:schedule:list", "值日安排查询");
        duty.put("duty:schedule:edit", "值日安排编辑");
        duty.put("duty:stat:list", "值日统计");
        duty.put("duty:reminder:config", "提醒配置");

        LinkedHashMap<String, String> common = new LinkedHashMap<>();
        common.put("common:notice:list", "公告查询");
        common.put("common:notice:edit", "公告维护");
        common.put("common:stat:list", "统计看板");
        common.put("common:video:list", "视频查询");
        common.put("common:video:edit", "视频维护");

        List<PermissionTreeVO> roots = new ArrayList<>();
        roots.add(group("用户与权限", "system", system));
        roots.add(group("代码仓库关联", "repo", repo));
        roots.add(group("部门预算", "budget", budget));
        roots.add(group("部门财产", "asset", asset));
        roots.add(group("部门工位", "seat", seat));
        roots.add(group("学长学姐展板", "honor", honor));
        roots.add(group("值日排班", "duty", duty));
        roots.add(group("通用支撑", "common", common));
        TREE = Collections.unmodifiableList(roots);

        LinkedHashSet<String> all = new LinkedHashSet<>();
        for (Map.Entry<String, String> entry : system.entrySet()) all.add(entry.getKey());
        for (Map.Entry<String, String> entry : repo.entrySet()) all.add(entry.getKey());
        for (Map.Entry<String, String> entry : budget.entrySet()) all.add(entry.getKey());
        for (Map.Entry<String, String> entry : asset.entrySet()) all.add(entry.getKey());
        for (Map.Entry<String, String> entry : seat.entrySet()) all.add(entry.getKey());
        for (Map.Entry<String, String> entry : honor.entrySet()) all.add(entry.getKey());
        for (Map.Entry<String, String> entry : duty.entrySet()) all.add(entry.getKey());
        for (Map.Entry<String, String> entry : common.entrySet()) all.add(entry.getKey());
        CODES = Collections.unmodifiableSet(all);
    }

    private PermissionCatalog() {
    }

    public static List<PermissionTreeVO> tree() {
        return TREE;
    }

    public static Set<String> codes() {
        return CODES;
    }

    private static PermissionTreeVO group(String label, String module, LinkedHashMap<String, String> children) {
        PermissionTreeVO root = new PermissionTreeVO();
        root.setLabel(label);
        root.setModule(module);
        List<PermissionTreeVO> nodes = new ArrayList<>(children.size());
        for (Map.Entry<String, String> entry : children.entrySet()) {
            PermissionTreeVO leaf = new PermissionTreeVO();
            leaf.setCode(entry.getKey());
            leaf.setLabel(entry.getValue());
            leaf.setModule(module);
            leaf.setChildren(List.of());
            nodes.add(leaf);
        }
        root.setChildren(nodes);
        return root;
    }
}