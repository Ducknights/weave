/**
 * 成员管理控制器
 * 提供社团成员的增删改查等基本操作
 */
package org.example.controller;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import org.example.entity.Member;
import org.example.model.ClubApiResponse;
import org.example.model.ClubApiStatus;
import org.example.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clubs/{clubId}/members")
public class MemberController {

    @Resource
    private MemberService memberService;

    /**
     * 创建成员
     * @param member 成员实体
     * @return 响应结果，包含创建的成员信息
     */
    @PostMapping()
    public ResponseEntity<ClubApiResponse<?>> createMember(@Nonnull @RequestBody Member member) {
        final Member memberVo = memberService.createMember(member);
        return ResponseEntity.status(ClubApiStatus.POST_SUCCESS.getCode())
                .body(ClubApiResponse.postSuccess(memberVo));
    }

    /**
     * 删除成员
     * @param memberId 成员ID
     * @return 响应结果
     */
    @DeleteMapping()
    public ResponseEntity<ClubApiResponse<?>> deleteMember(@Nonnull @RequestBody Integer memberId) {
        memberService.deleteMember(memberId);
        return ResponseEntity.status(ClubApiStatus.DELETE_SUCCESS.getCode())
                .body(ClubApiResponse.deleteSuccess());
    }

    /**
     * 更新成员信息
     * @param member 成员实体
     * @return 响应结果，包含更新后的成员信息
     */
    @PutMapping()
    public ResponseEntity<ClubApiResponse<?>> updateMember(@Nonnull @RequestBody Member member) {
        final Member newMember = memberService.updateMember(member);
        return ResponseEntity.status(ClubApiStatus.PUT_SUCCESS.getCode())
                .body(ClubApiResponse.putSuccess(newMember));
    }

    /**
     * 查询所有成员
     * @return 响应结果，包含所有成员列表
     */
    @GetMapping()
    public ResponseEntity<ClubApiResponse<?>> getMembers() {
        final List<Member> members = memberService.queryMembers();
        return ResponseEntity.status(ClubApiStatus.GET_SUCCESS.getCode())
                .body(ClubApiResponse.getSuccess(members));
    }

    /**
     * 根据ID获取成员信息
     * @param memberId 成员ID
     * @return 响应结果，包含指定ID的成员信息
     */
    @GetMapping("{memberId}")
    public ResponseEntity<ClubApiResponse<?>> getMemberById(@PathVariable Integer memberId) {
        final Member member = memberService.getMemberById(memberId);
        return ResponseEntity.status(ClubApiStatus.GET_SUCCESS.getCode())
                .body(ClubApiResponse.getSuccess(member));
    }

    /**
     * 根据社团ID获取成员列表
     * @param clubId 社团ID
     * @return 响应结果，包含指定社团的所有成员列表
     */
    @GetMapping("/{clubId}")
    public ResponseEntity<ClubApiResponse<?>> getMembersByClubId(@PathVariable Integer clubId) {
        final List<Member> members = memberService.getMembersByClubId(clubId);
        return ResponseEntity.status(ClubApiStatus.GET_SUCCESS.getCode())
                .body(ClubApiResponse.getSuccess(members));
    }

    /**
     * 根据用户ID获取成员信息
     * @param userId 用户ID
     * @return 响应结果，包含指定用户的所有成员信息
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ClubApiResponse<?>> getMembersByUserId(@PathVariable Integer userId) {
        final List<Member> members = memberService.getMembersByUserId(userId);
        return ResponseEntity.status(ClubApiStatus.GET_SUCCESS.getCode())
                .body(ClubApiResponse.getSuccess(members));
    }
}