package com.akaa07.java.database.factory.core;

import java.util.ArrayList;
import java.util.HashMap;

import com.ninja_squad.dbsetup.destination.DataSourceDestination;

public class RelationBuilder extends AbstractBuilder<RelationData>
{
	/** リレーション定義 */
	private RelationDefine def = null;

	/**
	 * コンストラクタ
	 *
	 * @param destination	データソース
	 * @param defineClass	リレーション定義クラス
	 * @param stackbox		データセットのスタック
	 * @return void
	 */
	public RelationBuilder(DataSourceDestination destination, Class<? extends AbstractDefine<RelationData>> defineClass, StackBox stackbox)
			throws Exception
	{
		this.dest = destination;
		this.def = (RelationDefine) RelationDefine.forClass(defineClass, stackbox);
	}

	/**
	 * リレーション定義を返却します。
	 *
	 * @return RelationDefine
	 */
	protected AbstractDefine<RelationData> getDefine()
	{
		return def;
	}

	/**
	 * データセットを蓄積し、データビルドを終了します。
	 *
	 * @return void
	 */
	public void stack()
	{
		def.stackbox.stack(this);
	}

	/**
	 * テーブルのセットアップを開始します。
	 * 終了後はendメソッドを呼び出してください。
	 *
	 * @param defineClass
	 * @return
	 * @throws Exception
	 */
	public Table table(Class<? extends TableDefine> defineClass) throws Exception
	{
		TableDefine tableDef = (TableDefine)TableDefine.forClass(defineClass, def.stackbox);
		return new Table(this, def.getTableDefineList(tableDef.getTableName()));
	}

	/**
	 * テーブル定義に登録されたデータセットの状態にします。
	 *
	 * @param name
	 * @return RelationBuilder
	 */
	public RelationBuilder state(String name) throws Exception
	{
		// stateが1つのステートメントで2回以上呼び出されたときにテーブルが重複してしまうのを防ぐ。
		def.clear();

		super.state(name);

		return this;
	}

	/**
	 * テーブル情報をセットアップするインナークラス
	 *
	 */
	public final class Table
	{
		/** ビルダークラス fluent interfaceを実現するために保持する。 */
		RelationBuilder builder;

		/** テーブルリスト */
		ArrayList<TableDefine> tables;

		/**
		 * コンストラクタ
		 *
		 * @param builder
		 * @param tables
		 */
		public Table(RelationBuilder builder, ArrayList<TableDefine> tables)
		{
			this.builder = builder;
			this.tables = tables;
		}

		/**
		 * レコードの編集を開始します。
		 * 終了後はendメソッドを呼び出してください。
		 *
		 * @param index
		 * @return
		 */
		public Row row(int index)
		{
			return new Row(this, tables.get(index));
		}

		/**
		 * 1レコード目の属性値を設定します。
		 * 特定のレコードを操作する場合にはrowメソッドを使用してください。
		 *
		 * @param String name 列名
		 * @param Object value 値
		 * @return
		 */
		public Table column(String name, Object value)
		{
			tables.get(0).column(name, value);

			return this;
		}

		/**
		 * 1レコード目の属性値を設定します。
		 * 特定のレコードを操作する場合にはrowメソッドを使用してください。
		 *
		 * @param HashMap<String, ?> データセット
		 * @return
		 */
		public Table column(HashMap<String, ?> values)
		{
			tables.get(0).column(values);

			return this;
		}

		/**
		 * テーブル情報のセットアップを終了します。
		 *
		 * @return RelationBuilder
		 */
		public RelationBuilder end()
		{
			return builder;
		}
	}

	/**
	 * レコードの編集を行うインナークラス
	 *
	 */
	public final class Row
	{
		Table table;
		TableDefine row;

		public Row(Table table, TableDefine row)
		{
			this.table = table;
			this.row = row;
		}

		/**
		 * レコードの属性値を設定します。
		 *
		 * @param String name 列名
		 * @param Object value 値
		 * @return
		 */
		public Row column(String name, Object value)
		{
			row.column(name, value);

			return this;
		}

		/**
		 * レコードの属性値を設定します。
		 *
		 * @param HashMap<String, ?> データセット
		 * @return
		 */
		public Row column(HashMap<String, ?> values)
		{
			row.column(values);

			return this;
		}

		/**
		 * レコードの編集を終了します。
		 *
		 * @return
		 */
		public Table end()
		{
			return table;
		}
	}
}
