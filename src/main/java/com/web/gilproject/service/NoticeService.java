package com.web.gilproject.service;

import com.web.gilproject.domain.Notice;
import com.web.gilproject.dto.NoticeDTO;
import com.web.gilproject.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;

    public List<NoticeDTO> getAllNotice() {
        List<Notice> all = noticeRepository.findAll();

        List<NoticeDTO> noticeList = new ArrayList<>();
        for (Notice notice : all) {
            NoticeDTO n = new NoticeDTO(notice.getTitle(),notice.getContent(),notice.getWriteDate());
            noticeList.add(n);
        }

        // getWriteDate 기준으로 내림차순 정렬 (최신 공지가 첫 번째로 오도록)
        Collections.sort(noticeList, new Comparator<NoticeDTO>() {
            @Override
            public int compare(NoticeDTO n1, NoticeDTO n2) {
                // 내림차순: n2의 날짜가 더 최근이면 -1을 리턴 (n2가 앞에 와야 함)
                return n2.getWriteDate().compareTo(n1.getWriteDate());
            }
        });

        return noticeList;
    }
}
