package littleMaidMobX.entity.modes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import littleMaidMobX.LittleMaidMobX;
import littleMaidMobX.entity.EntityLittleMaid;
import mmmlibx.lib.FileManager;
import mmmlibx.lib.MMM_ManagerBase;
import mmmlibx.lib.rewrite.RewritedFileManager;

public class EntityModeManager extends MMM_ManagerBase {
	public static class EntityPriorityComparator implements Comparator<EntityModeBase> {
		@Override
		public int compare(EntityModeBase o1, EntityModeBase o2) {
			return o1.priority() - o2.priority();
		}
	}

	public static final String prefix = "EntityMode";
	public static final List<EntityModeBase> maidModeList = new ArrayList<>();

	public static void init() {
		// 特定名称をプリフィックスに持つmodファイをを獲得
		//FileManager.getModFile("EntityMode", prefix);

		RewritedFileManager.searchFile("EntityMode", prefix);
	}
	
	public static void loadEntityMode() {
		(new EntityModeManager()).load();
		maidModeList.sort(new EntityPriorityComparator());
	}

	@Override
	protected String getPreFix() {
		return prefix;
	}

	@Override
	protected boolean append(Class pclass) {
		// プライオリティー順に追加
		// ソーター使う？
		if (!EntityModeBase.class.isAssignableFrom(pclass)) {
			return false;
		}
		
		try {
			EntityModeBase lemb = null;
			lemb = (EntityModeBase)pclass.getConstructor(EntityLittleMaid.class).newInstance((EntityLittleMaid)null);
			lemb.init();
			maidModeList.add(lemb);
			return true;
		} catch (Exception e) {
			LittleMaidMobX.debug("Failed to load Entity Mode class %s!" + pclass.getName());

			e.printStackTrace();
		}

		return false;
	}

	/**
	 * AI追加用のリストを獲得。 
	 */
	public static List<EntityModeBase> getModeList(EntityLittleMaid littleMaid) {
		List<EntityModeBase> list = new ArrayList<>();
		for (EntityModeBase modes : maidModeList) {
			try {
				list.add(modes.getClass().getConstructor(EntityLittleMaid.class).newInstance(littleMaid));
			} catch (Exception | Error e) {
				e.printStackTrace();
			}
        }
		return list;
	}

	/**
	 * ロードされているモードリストを表示する。
	 */
	public static void showLoadedModes() {
		LittleMaidMobX.debug("Loaded Mode lists(%d)", maidModeList.size());
		for (EntityModeBase lem : maidModeList) {
			LittleMaidMobX.debug("%04d : %s", lem.priority(), lem.getClass().getSimpleName());
		}
	}

}
