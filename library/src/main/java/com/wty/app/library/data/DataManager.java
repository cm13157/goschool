package com.wty.app.library.data;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class DataManager {

    public static class SqlQueryBuilder{

        public List<String> sqlquery = new ArrayList<String>();

        public SqlQueryBuilder(){
            sqlquery.clear();
        }

        /**
         * @Decription 选择所有列
         **/
        public SqlQueryBuilder select(){
            select("*");
            return this;
        }

        /**
         * @Decription 选择特定列
         * @param columnname 列名
         **/
        public SqlQueryBuilder select(String... columnname){
            List<String> list = new ArrayList<String>();
            for(String column:columnname){
                list.add(column);
            }
            sqlquery.add(TextUtils.join(",", list));
            return this;
        }

        /**
         * @Decription 待查询的表
         * @param tablename 表名
         **/
        public SqlQueryBuilder from(String tablename){
            sqlquery.add(tablename);
            return this;
        }

        /**
         * @Decription where子句
         * @param where 条件
         **/
        public SqlQueryBuilder where(String where){
            sqlquery.add(where);
            return this;
        }

        /**
         * @Decription and子句
         * @param where and后面添加的条件
         **/
        public SqlQueryBuilder and(String where){
            sqlquery.add(where);
            return this;
        }

        /**
         * @Decription leftjoin语句
         **/
        public SqlQueryBuilder leftJoin(){
            return this;
        }

        /**
         * @Decription 生成sqlite语句
         **/
        public String Builder(){
            return TextUtils.join(" ",sqlquery);
        }
    }

}
