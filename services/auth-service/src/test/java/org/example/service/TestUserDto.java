package org.example.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 复杂测试对象，用于验证 Redis 缓存的 JSON 序列化/反序列化
 */
public class TestUserDto {

    private Long id;
    private String name;
    private Integer age;
    private String email;
    private Boolean active;
    private Double score;
    private LocalDateTime createTime;
    private List<String> tags;
    private List<Address> addresses;
    private Map<String, Object> extInfo;

    public TestUserDto() {}

    public TestUserDto(Long id, String name, Integer age, String email, Boolean active,
                       Double score, LocalDateTime createTime, List<String> tags,
                       List<Address> addresses, Map<String, Object> extInfo) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
        this.active = active;
        this.score = score;
        this.createTime = createTime;
        this.tags = tags;
        this.addresses = addresses;
        this.extInfo = extInfo;
    }

    // ===== getters & setters =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public List<Address> getAddresses() { return addresses; }
    public void setAddresses(List<Address> addresses) { this.addresses = addresses; }

    public Map<String, Object> getExtInfo() { return extInfo; }
    public void setExtInfo(Map<String, Object> extInfo) { this.extInfo = extInfo; }

    // ===== 嵌套对象 =====

    public static class Address {
        private String province;
        private String city;
        private String detail;
        private Integer zipCode;

        public Address() {}

        public Address(String province, String city, String detail, Integer zipCode) {
            this.province = province;
            this.city = city;
            this.detail = detail;
            this.zipCode = zipCode;
        }

        public String getProvince() { return province; }
        public void setProvince(String province) { this.province = province; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getDetail() { return detail; }
        public void setDetail(String detail) { this.detail = detail; }

        public Integer getZipCode() { return zipCode; }
        public void setZipCode(Integer zipCode) { this.zipCode = zipCode; }
    }
}
