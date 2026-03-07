/**
 * 社团管理控制器
 * 提供社团的增删改查等基本操作
 */
package org.example.controller;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import org.example.entity.Club;
import org.example.model.vo.ClubCardVo;
import org.example.model.ClubApiResponse;
import org.example.model.ClubApiStatus;
import org.example.service.ClubService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<ClubApiResponse<?>> createClub(@Nonnull @RequestBody Club club) {
        final Club newClub = clubService.createClub(club);
        return ResponseEntity.status(ClubApiStatus.POST_SUCCESS.getCode())
                .body(ClubApiStatus.POST_SUCCESS.response(newClub));
    }

    /**
     * 删除俱乐部
     *
     * @param clubId 俱乐部ID
     * @return 删除成功
     */
    @DeleteMapping()
    public ResponseEntity<ClubApiResponse<?>> deleteClub(@Nonnull @RequestBody Integer clubId) {
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
    public ResponseEntity<ClubApiResponse<?>> updateClub(@Nonnull @RequestBody Club club) {
        final Club newClub = clubService.updateClub(club);
        return ResponseEntity.status(ClubApiStatus.PUT_SUCCESS.getCode())
                .body(ClubApiStatus.PUT_SUCCESS.response(newClub));
    }

    /**
     * 查询所有俱乐部
     *
     * @return 所有俱乐部信息
     */
    @GetMapping("/clubs")
    public ResponseEntity<ClubApiResponse<?>> getClubs() {
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
    public ResponseEntity<ClubApiResponse<?>> getClubById(@PathVariable Integer clubId) {
        final Club club = clubService.getClubById(clubId);
        return ResponseEntity.status(ClubApiStatus.GET_SUCCESS.getCode())
                .body(ClubApiStatus.GET_SUCCESS.response(club));
    }
}