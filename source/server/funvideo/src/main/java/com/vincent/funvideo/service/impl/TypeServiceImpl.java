package com.vincent.funvideo.service.impl;

import com.vincent.funvideo.db.dao.TypeDao;
import com.vincent.funvideo.db.pojo.Type;
import com.vincent.funvideo.service.TypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Scope("prototype")
@Slf4j
public class TypeServiceImpl implements TypeService {

    @Autowired
    private TypeDao typeDao;
    @Override
    public List<Type> searchAllTypes() {
        return typeDao.searchAllTypes();
    }
}
