package es.acperez.domocontrol.modules.actions;

import es.acperez.domocontrol.modules.base.DomoController;


public abstract class DomoActionController extends DomoController {
	
	public DomoActionController(int id, String name) {
		super(id, name, DomoController.TYPE_ACTION);
	}
	
}
