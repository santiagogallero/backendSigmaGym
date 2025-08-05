package com.sigma.gym.services;

import com.sigma.gym.model.Progress;
import java.util.List;

public interface ProgressService {
    Progress createProgress(Progress progress);
    Progress getProgressById(Long id);
    List<Progress> getAllProgress();
    Progress updateProgress(Long id, Progress progress);
    void deleteProgress(Long id);
}
