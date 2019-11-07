import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * 场景管理，双向十字链表
 * @author Administrator
 *
 */
public class AOIScreen {

    private static class AOINode {
	// 前一个x
	public AOINode xPrev;
	// 后一个x
	public AOINode xNext;
	// 前一个y
	public AOINode yPrev;
	// 后一个y
	public AOINode yNext;

	// x值比自己小的最前一个
	public AOINode xPrevUnlike;
	// y值比自己小的最前一个
	public AOINode yPrevUnlike;
	// x值比自己大的最前一个
	public AOINode xNextUnlike;
	// y值比自己大的最前一个
	public AOINode yNextUnlike;

	// 当前x
	public int x;
	// 当前y
	public int y;
	// id
	public final long id;

	public AOINode(int x, int y, long id) {
	    this.x = x;
	    this.y = y;
	    this.id = id;
	}
    }

    // 链表头x
    private AOINode xStart;
    // 链表尾x
    private AOINode xEnd;

    // 链表头y
    private AOINode yStart;
    // 链表尾y
    private AOINode yEnd;

    // 链表x第一个最大值（性能优化辅助用）比如链表=1,2,2,3,4,5,5,5 此时xMaxFirsts为倒数第3个5
    private AOINode xMaxFirst;
    // 链表y第一个最大值（性能优化辅助用）比如链表=1,2,2,3,4,5,5,5 此时yMaxFirsts为倒数第3个5
    private AOINode yMaxFirst;
    //结点数
    private int count;

    public AOIScreen() {

    }

    /**
     * 插入addNode到preNode后面
     * 
     * @param addNode
     * @param preNode
     */
    private final void _addx(AOINode addNode, AOINode preNode) {
	if (addNode == null) {
	    return;
	}
	if (preNode == null) {
	    if (xStart == null) {
		// 第一个元素
		xStart = addNode;
	    } else {
		// 插入到链表头前面
		addNode.xNext = xStart;
		xStart.xPrev = addNode;
		addNode.xNextUnlike = xStart;
		xStart.xPrevUnlike = addNode;
		xStart = addNode;
	    }
	} else if (!addNode.equals(preNode)) {
	    if (preNode.xNext != null) {
		// 不是插入到链表尾
		preNode.xNext.xPrev = addNode;
	    }
	    addNode.xNext = preNode.xNext;
	    preNode.xNext = addNode;
	    addNode.xPrev = preNode;

	    if (addNode.x != preNode.x) {
		if (addNode.xNext != null) {
		    addNode.xNextUnlike = addNode.xNext;
		    addNode.xPrevUnlike = addNode.xNext.xPrevUnlike;
		    if (addNode.xNext.xPrevUnlike != null) {
			addNode.xNext.xPrevUnlike.xNextUnlike = addNode;
		    }
		    addNode.xNext.xPrevUnlike = addNode;
		} else if (xMaxFirst != null && xMaxFirst.x != addNode.x) {
		    xMaxFirst.xNextUnlike = addNode;
		    addNode.xPrevUnlike = xMaxFirst;
		} else {
		    System.out.println("ewer");
		}
	    }
	}
	if (addNode.xNext == null) {
	    xEnd = addNode;
	}
	if (xMaxFirst == null || (xMaxFirst.x < addNode.x)) {
	    xMaxFirst = addNode;
	}
    }

    private final void _addy(AOINode addNode, AOINode preNode) {
	if (addNode == null) {
	    return;
	}
	if (preNode == null) {
	    if (yStart == null) {
		yStart = addNode;
	    } else {
		addNode.yNext = yStart;
		yStart.yPrev = addNode;
		addNode.yNextUnlike = yStart;
		yStart.yPrevUnlike = addNode;
		yStart = addNode;
	    }
	} else if (!addNode.equals(preNode)) {
	    if (preNode.yNext != null) {
		preNode.yNext.yPrev = addNode;
	    }
	    addNode.yNext = preNode.yNext;
	    preNode.yNext = addNode;
	    addNode.yPrev = preNode;
	    if (addNode.y != preNode.y) {
		if (addNode.yNext != null) {
		    addNode.yNextUnlike = addNode.yNext;
		    addNode.yPrevUnlike = addNode.yNext.yPrevUnlike;
		    if (addNode.yNext.yPrevUnlike != null) {
			addNode.yNext.yPrevUnlike.yNextUnlike = addNode;
		    }
		    addNode.yNext.yPrevUnlike = addNode;
		} else if (yMaxFirst != null && yMaxFirst.y != addNode.y) {
		    yMaxFirst.yNextUnlike = addNode;
		    addNode.yPrevUnlike = yMaxFirst;
		}
	    }
	}
	if (addNode.yNext == null) {
	    yEnd = addNode;
	}
	if (yMaxFirst == null || yMaxFirst.y < addNode.y) {
	    yMaxFirst = addNode;
	}
    }

    int maxFindCount = 0;

    /**
     * 从node开始查找待插入x位置的前一个node
     * 
     * @param node
     * @param x
     * @return
     */
    private final AOINode _findPreX(int x) {
	AOINode node = xStart;
	if (xStart != null && xEnd != null && xMaxFirst != null) {
	    if (x >= xEnd.x) {
		node = xEnd;
	    } else {
		// 过滤掉重复元素，尽快找到更接近的父节点
		if (Math.abs(x - xStart.x) >= Math.abs(x - xEnd.x)) {
		    if (xStart.xNextUnlike != null) {
			node = xStart;
			if (node.x <= x) {
			    while (node.x <= x && node.xNextUnlike != null) {
				node = node.xNextUnlike;
			    }
			}
		    }
		} else {
		    if (xMaxFirst.xPrevUnlike != null) {
			node = xMaxFirst;
			AOINode last = node;
			while (last != null && last.x > x) {
			    node = last;
			    last = last.xPrevUnlike;
			}
		    }
		}
	    }
	}
	AOINode cur = _findX(node, x);
	return cur;
    }

    private final AOINode _findPreY(int y) {
	AOINode node = yStart;
	if (yStart != null && yEnd != null && yMaxFirst != null) {
	    if (y >= yEnd.y) {
		node = yEnd;
	    } else {
		// 过滤掉重复元素，尽快找到更接近的父节点
		if (Math.abs(y - yStart.y) >= Math.abs(y - yEnd.y)) {
		    if (yStart.yNextUnlike != null) {
			node = yStart;
			if (node.y <= y) {
			    while (node.y <= y && node.yNextUnlike != null) {
				node = node.yNextUnlike;
			    }
			}
		    }
		} else {
		    if (yMaxFirst.yPrevUnlike != null) {
			node = yMaxFirst;
			AOINode last = node;
			while (last != null && last.y > y) {
			    node = last;
			    last = last.yPrevUnlike;
			}
		    }
		}
	    }
	}
	AOINode cur = _findY(node, y);
	return cur;
    }

    private final AOINode _findX(AOINode node, int x) {
	AOINode cur = null;
	if (node != null) {
	    // 逐步寻找最合适的父节点
	    if (node.x <= x) {
		do {
		    cur = node;
		    node = node.xNext;
		} while (node != null && node.x <= x);
	    } else if (node.x > x) {
		while ((node = node.xPrev) != null) {
		    if (node.x <= x) {
			cur = node;
			break;
		    }
		}
	    }
	}
	return cur;
    }

    private final AOINode _findY(AOINode node, int y) {
	AOINode cur = null;
	if (node != null) {
	    // 逐步寻找最合适的父节点
	    if (node.y <= y) {
		do {
		    cur = node;
		    node = node.yNext;
		} while (node != null && node.y <= y);
	    } else if (node.y > y) {
		while ((node = node.yPrev) != null) {
		    if (node.y <= y) {
			cur = node;
			break;
		    }
		}
	    }
	}
	return cur;
    }

    private final void _delx(AOINode node) {
	if (node.equals(xStart)) {
	    xStart = node.xNext;
	}
	if (node.equals(xEnd)) {
	    xEnd = node.xPrev;
	}
	if (node.xPrev != null) {
	    node.xPrev.xNext = node.xNext;
	}
	if (node.xNext != null) {
	    node.xNext.xPrev = node.xPrev;
	}
	if (node.xPrevUnlike != null) {
	    node.xPrevUnlike.xNextUnlike = node.xNext;
	}
	if (node.equals(xMaxFirst)) {
	    if (node.xNext != null) {
		xMaxFirst = node.xNext;
		xMaxFirst.xPrevUnlike = node.xPrevUnlike;
		xMaxFirst.xNextUnlike = node.xNextUnlike;
	    } else if (node.xPrevUnlike != null) {
		xMaxFirst = node.xPrevUnlike;
	    }
	}
	if (node.xNextUnlike != null) {
	    if (node.xNext.x != node.xNextUnlike.x) {
		node.xNext.xPrevUnlike = node.xPrevUnlike;
		node.xNext.xNextUnlike = node.xNextUnlike;
		node.xNextUnlike.xPrevUnlike = node.xNext;
	    } else {
		node.xNextUnlike.xPrevUnlike = node.xPrevUnlike;
	    }
	}

	node.xPrevUnlike = null;
	node.xNextUnlike = null;
	node.xNext = null;
	node.xPrev = null;
    }

    private final void _dely(AOINode node) {
	if (node.equals(yStart)) {
	    yStart = node.yNext;
	}
	if (node.equals(yEnd)) {
	    yEnd = node.yPrev;
	}
	if (node.yPrev != null) {
	    node.yPrev.yNext = node.yNext;
	}
	if (node.yNext != null) {
	    node.yNext.yPrev = node.yPrev;
	}
	if (node.yPrevUnlike != null) {
	    node.yPrevUnlike.yNextUnlike = node.yNext;
	}
	if (node.equals(yMaxFirst)) {
	    if (node.yNext != null) {
		yMaxFirst = node.yNext;
		yMaxFirst.yPrevUnlike = node.yPrevUnlike;
		yMaxFirst.yNextUnlike = node.yNextUnlike;
	    } else if (node.yPrevUnlike != null) {
		yMaxFirst = node.yPrevUnlike;
	    }
	}
	if (node.yNextUnlike != null) {
	    if (node.yNext.y != node.yNextUnlike.y) {
		node.yNext.yPrevUnlike = node.yPrevUnlike;
		node.yNext.yNextUnlike = node.yNextUnlike;
		node.yNextUnlike.yPrevUnlike = node.yNext;
	    } else {
		node.yNextUnlike.yPrevUnlike = node.yPrevUnlike;
	    }
	}

	node.yPrevUnlike = null;
	node.yNextUnlike = null;
	node.yNext = null;
	node.yPrev = null;
    }

    public void add(AOINode node) {
	AOINode curx = _findPreX(node.x);
	_addx(node, curx);
	AOINode cury = _findPreY(node.y);
	_addy(node, cury);
	count++;

    }

    public void change(AOINode node, int x, int y) {

	if (node.x != x) {
	    AOINode curx = _findPreX(x);
	    if (curx != null && !curx.equals(node)) {
		_delx(node);
		node.x = x;
		_addx(node, curx);
	    }
	}
	if (node.y != y) {
	    AOINode cury = _findPreY(y);
	    if (cury != null && !cury.equals(node)) {
		_dely(node);
		node.y = y;
		_addy(node, cury);
	    }
	}
    }

    // move一般移动的距离比较短,之所以把chang和move分开，在真实的游戏场景里面，直接move应该更快的，因为结点分布比较稀疏
    public void move(AOINode node, int x, int y) {
	if (node.x != x) {
	    AOINode curx = _findX(node, x);
	    if (curx != null && !curx.equals(node)) {
		_delx(node);
		node.x = x;
		_addx(node, curx);
	    }
	}
	if (node.y != y) {
	    AOINode cury = _findY(node, y);
	    if (cury != null && !cury.equals(node)) {
		_dely(node);
		node.y = y;
		_addy(node, cury);
	    }
	}
    }

    public void del(AOINode node) {
	if (node != null) {
	    _delx(node);
	    _dely(node);
	    count--;
	}
    }

    /**
     * 检查链表是否有效，指针信息是否错乱
     * 
     * @return
     */
    public boolean checkValid() {
	AOINode curX = this.xStart;
	if (xEnd == null || xStart == null || xMaxFirst == null) {
	    System.out.println("error:" + 1);
	    return false;
	}
	do {
	    if (curX.xPrevUnlike != null) {
		if (curX.xPrevUnlike.x >= curX.x) {
		    System.out.println("error:" + 1);
		    return false;
		}
	    }
	    if (curX.xNextUnlike != null) {
		if (curX.xNextUnlike.x <= curX.x) {
		    System.out.println("error:" + 2);
		    return false;
		}
	    }
	    if (curX.xNext != null) {
		if (curX.x > curX.xNext.x) {
		    System.out.println("error:" + 3);
		    return false;
		}
	    }
	    if (curX.xPrev != null && curX.xPrev.x == curX.x && (curX.xPrevUnlike != null || curX.xNextUnlike != null)) {
		System.out.println("error:" + 4);
		return false;
	    }
	    if (xMaxFirst != null && xMaxFirst.xPrev != null && xMaxFirst.xPrev.x == xMaxFirst.x) {
		System.out.println("error:" + 5);
		return false;
	    }
	    if (xStart != null && xEnd == null) {
		System.out.println("error:" + 6);
		return false;
	    }
	} while ((curX = curX.xNext) != null);

	if (xStart != null && xMaxFirst != null) {
	    if (xStart.x != xMaxFirst.x) {
		if (xMaxFirst.xPrevUnlike == null) {
		    System.out.println("error:" + 7);
		    return false;
		}
	    }
	}
	return true;
    }

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append("xend=================" + xEnd.id + ":" + xEnd.x + "\n");
	AOINode curX = this.xStart;
	do {
	    if (curX.xNextUnlike != null) {
		sb.append(curX.id + ":   " + curX.x + "->" + curX.xNextUnlike.x + "\n");
	    } else {
		sb.append(curX.id + ":   " + curX.x + "->" + "\n");
	    }
	} while ((curX = curX.xNext) != null);
	return sb.toString();
    }

    public static void main(String[] args) {
	// 热身开始
	for (int i = 0; i < 2000; i++) {
	    Math.pow(2, 10000);
	}
	Random rdm = new Random();
	long s = System.currentTimeMillis();
	int c = 0;
	int n = 10000;
	while (c++ < 10) {
	    int rang = 200 * c;
	    int nodenum = 1000 * c;

	    System.out.println(String.format("结点数:%d,稀疏程度(%d),区域大小:%d*%d,move/change执行次数:%d,c:%d=========================", nodenum, (rang * rang) / nodenum, rang, rang, n, c));
	    AOIScreen aoiScreen = new AOIScreen();
	    List<AOINode> nodeCash = new ArrayList<>();
	    for (int i = 0; i < nodenum; i++) {
		int x = Math.abs(rdm.nextInt()) % rang;
		int y = Math.abs(rdm.nextInt()) % rang;
		AOINode aoiNode = new AOIScreen.AOINode(x, y, i);
		aoiScreen.add(aoiNode);
		nodeCash.add(aoiNode);
		if (!aoiScreen.checkValid()) {
		    System.out.println("error");
		    break;
		}
	    }
	    if (aoiScreen.checkValid()) {
		System.out.println("add耗时：" + (System.currentTimeMillis() - s));
	    } else {
		System.out.println("add-error");
	    }
	    s = System.currentTimeMillis();
	    for (int i = 0; i < n; i++) {
		int idx = Math.abs(rdm.nextInt()) % nodeCash.size();
		AOINode aoiNode = nodeCash.get(idx);
		int x = rdm.nextInt() % rang;
		int y = rdm.nextInt() % rang;
		aoiScreen.change(aoiNode, x, y);
		if (!aoiScreen.checkValid()) {
		    System.out.println("change-error:");
		    break;
		}
	    }
	    if (aoiScreen.checkValid()) {
		System.out.println("change耗时：" + (System.currentTimeMillis() - s));
	    }
	    s = System.currentTimeMillis();
	    for (int i = 0; i < n; i++) {
		int idx = Math.abs(rdm.nextInt()) % nodeCash.size();
		AOINode aoiNode = nodeCash.get(idx);
		// 移动一般都是一步一步的走
		int x = rdm.nextInt() % 2;
		int y = rdm.nextInt() % 2;
		aoiScreen.move(aoiNode, x, y);
		if (!aoiScreen.checkValid()) {
		    System.out.println("moveerror:");
		    break;
		}
	    }
	    if (aoiScreen.checkValid()) {
		System.out.println("move耗时：" + (System.currentTimeMillis() - s));
	    }
	    s = System.currentTimeMillis();

	    for (int i = 0; i < nodeCash.size() - 1; i++) {
		int idx = Math.abs(rdm.nextInt()) % nodeCash.size();
		AOINode remove = nodeCash.remove(idx);
		aoiScreen.del(remove);
		if (!aoiScreen.checkValid()) {
		    System.out.println("del-error:");
		    break;
		}
	    }
	    if (aoiScreen.checkValid()) {
		System.out.println("del耗时：" + (System.currentTimeMillis() - s));
	    }

	}
    }
}