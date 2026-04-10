package io.ola.crud.query;

import io.ola.crud.model.SortField;
import io.ola.crud.model.TestEntity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SortHelper单元测试
 *
 * @author yiuman
 * @date 2026/3/26
 */
class SortHelperTest {

    @Test
    void testParseSort_singleFieldAsc() {
        List<SortField> result = SortHelper.parseSort(TestEntity.class, "createdAt");

        assertEquals(1, result.size());
        assertEquals("created_at", result.get(0).column());
        assertFalse(result.get(0).descending());
    }

    @Test
    void testParseSort_singleFieldDesc() {
        List<SortField> result = SortHelper.parseSort(TestEntity.class, "-createdAt");

        assertEquals(1, result.size());
        assertEquals("created_at", result.get(0).column());
        assertTrue(result.get(0).descending());
    }

    @Test
    void testParseSort_multipleFields() {
        List<SortField> result = SortHelper.parseSort(TestEntity.class, "createdAt,-updatedAt,username");

        assertEquals(3, result.size());

        assertEquals("created_at", result.get(0).column());
        assertFalse(result.get(0).descending());

        assertEquals("updated_at", result.get(1).column());
        assertTrue(result.get(1).descending());

        assertEquals("username", result.get(2).column());
        assertFalse(result.get(2).descending());
    }

    @Test
    void testParseSort_emptyString() {
        List<SortField> result = SortHelper.parseSort(TestEntity.class, "");

        assertTrue(result.isEmpty());
    }

    @Test
    void testParseSort_null() {
        List<SortField> result = SortHelper.parseSort(TestEntity.class, null);

        assertTrue(result.isEmpty());
    }

    @Test
    void testParseSort_withSpaces() {
        List<SortField> result = SortHelper.parseSort(TestEntity.class, " createdAt , -updatedAt ");

        assertEquals(2, result.size());

        assertEquals("created_at", result.get(0).column());
        assertFalse(result.get(0).descending());

        assertEquals("updated_at", result.get(1).column());
        assertTrue(result.get(1).descending());
    }

    @Test
    void testParseSort_emptyBetweenCommas() {
        List<SortField> result = SortHelper.parseSort(TestEntity.class, "createdAt,,updatedAt");

        assertEquals(2, result.size());

        assertEquals("created_at", result.get(0).column());
        assertEquals("updated_at", result.get(1).column());
    }

    @Test
    void testParseSort_onlyMinus() {
        List<SortField> result = SortHelper.parseSort(TestEntity.class, "-");

        assertTrue(result.isEmpty());
    }

    @Test
    void testParseSort_notExistField() {
        // 不存在的字段会被跳过
        List<SortField> result = SortHelper.parseSort(TestEntity.class, "-notExist,,createdAt");

        assertEquals(1, result.size());
        assertEquals("created_at", result.get(0).column());
        assertFalse(result.get(0).descending());  // createdAt 无 - 前缀，升序
    }
}
