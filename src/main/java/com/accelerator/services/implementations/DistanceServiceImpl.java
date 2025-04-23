package com.accelerator.services.implementations;

import com.accelerator.services.DistanceService;
import org.springframework.stereotype.Service;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

@Service("distanceService")
public class DistanceServiceImpl implements DistanceService {

    @Override
    public Double countDistance(Double[] firstCoordinates, Double[] secondCoordinates) {
        return sqrt(pow(firstCoordinates[0] - secondCoordinates[0], 2) +
                pow(firstCoordinates[1] - secondCoordinates[1], 2) +
                pow(firstCoordinates[2] - secondCoordinates[2], 2));
    }
}
