/**
 * 社团管理控制器
 * 提供社团的增删改查等基本操作
 */
package org.example.controller;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import org.example.dto.ClubBriefDto;
import org.example.model.entity.Club;
import org.example.model.vo.ClubCardVo;
import org.example.model.enums.ClubApiStatus;
import org.example.service.ClubService;
import org.example.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/club")
public class ClubController {

    @Resource
    private ClubService clubService;

    /**
     * 创建俱乐部
     *
     * @param club 俱乐部信息
     * @return 创建成功的俱乐部信息
     */
    @PostMapping()
    public ResponseEntity<?> createClub(@Nonnull @RequestBody Club club) {
        final Club newClub = clubService.createClub(club);
        return ResponseEntity.status(ClubApiStatus.POST_SUCCESS.getCode())
                .body(ClubApiStatus.POST_SUCCESS.response(newClub));
    }

    /**
     * 加入俱乐部
     */
    @PostMapping("/join")
    public ResponseEntity<?> joinClub(@RequestParam Integer clubId) {
        Long userId = SecurityUtils.getCurrentUserId();
        clubService.joinClub(clubId, userId);
        return ResponseEntity.status(ClubApiStatus.POST_SUCCESS.getCode())
                .body(ClubApiStatus.POST_SUCCESS.response());
    }

    /**
     * 删除俱乐部
     *
     * @param clubId 俱乐部ID
     * @return 删除成功
     */
    @DeleteMapping()
    public ResponseEntity<?> deleteClub(@Nonnull @RequestBody Integer clubId) {
        clubService.deleteClub(clubId);
        return ResponseEntity.status(ClubApiStatus.DELETE_SUCCESS.getCode())
                .body(ClubApiStatus.DELETE_SUCCESS.response());
    }

    /**
     * 更新俱乐部信息
     *
     * @param club 俱乐部信息
     * @return 更新成功的俱乐部信息
     */
    @PutMapping()
    public ResponseEntity<?> updateClub(@Nonnull @RequestBody Club club) {
        final Club newClub = clubService.updateClub(club);
        return ResponseEntity.status(ClubApiStatus.PUT_SUCCESS.getCode())
                .body(ClubApiStatus.PUT_SUCCESS.response(newClub));
    }

    /**
     * 批量查询
     */
    @PostMapping("/batch")
    public Map<Long, ClubBriefDto> batchJoinClub(@RequestBody @Nonnull List<Integer> clubIds) {
        return clubService.batchClubsById(clubIds);
    }

    /**
     * 查询所有俱乐部
     *
     * @return 所有俱乐部信息
     */
    @GetMapping("/clubs")
    public ResponseEntity<?> getClubs() {
        final List<ClubCardVo> clubCardVos = clubService.queryClubs();
        return ResponseEntity.status(ClubApiStatus.GET_SUCCESS.getCode())
                .body(ClubApiStatus.GET_SUCCESS.response(clubCardVos));
    }

    /**
     * 根据ID查询俱乐部
     *
     * @param clubId 俱乐部ID
     * @return 俱乐部信息
     */
    @GetMapping("{clubId}")
    public ResponseEntity<?> getClubById(@PathVariable Integer clubId) {
        final ClubCardVo club = clubService.getClubById(clubId);
        return ResponseEntity.status(ClubApiStatus.GET_SUCCESS.getCode())
                .body(ClubApiStatus.GET_SUCCESS.response(club));
    }

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok().body("服务运行正常");
    }
}