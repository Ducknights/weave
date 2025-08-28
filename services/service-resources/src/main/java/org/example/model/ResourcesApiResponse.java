package org.example.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResourcesApiResponse<T> extends ApiResponse<T>{

}
