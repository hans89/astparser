package astparser.visitor;

import org.eclipse.jdt.core.dom.*;
import java.util.*;
import java.util.Map.Entry;

public class VariableDeclarationVisitor extends ASTVisitor {
	private HashMap<String, DeclarationInfo> declarations;

	public VariableDeclarationVisitor(String[] classKeys) {
		
		declarations = new HashMap<String, DeclarationInfo>(classKeys.length);

		for (String key : classKeys) {
			declarations.put(key, new DeclarationInfo(key));
		}
	}

	// @TODO: check array declaration

	@Override
	public boolean visit(VariableDeclarationFragment node) {
		checkAndAdd(node);
		return false;
	}

	@Override
	public boolean visit(SingleVariableDeclaration node) {
		checkAndAdd(node);
		return false;
	}

	private void checkAndAdd(VariableDeclaration node) {
		IVariableBinding vbind = node.resolveBinding();
		if (vbind != null) {
			ITypeBinding tbind = vbind.getType();
			if (tbind != null) {
				String key = matchSuperClass(tbind);
				if (key != null) {
					if (vbind.isField())
						declarations.get(key).addField(node);
					else if (vbind.isParameter())
						declarations.get(key).addFormalArg(node);
					else 
						declarations.get(key).addLocalVar(node);
				}
			}
		}
	}

	private String matchSuperClass(ITypeBinding tbind) {
		while (tbind != null && tbind.getKey() != "Ljava/lang/Object;") {
			String key = tbind.getKey();
			if (declarations.containsKey(key))
				return key;
			tbind = tbind.getSuperclass();
		}
		
		return null;
	}

	public Set<DeclarationInfo> getNonEmpty() {
		Set<String> keys = declarations.keySet();
		Set<DeclarationInfo> infos = new HashSet<DeclarationInfo>();
		for (String key : keys) {
			if (declarations.get(key).isEmpty() == false)
				infos.add(declarations.get(key));
		}
		return infos;
	}

	public static class DeclarationInfo {
		private List<VariableDeclaration> fields;
		private List<VariableDeclaration> localVars;
		private List<VariableDeclaration> formalArgs;
		private String classKey;
		public DeclarationInfo(String key) {
			classKey = key;
			fields = new ArrayList<VariableDeclaration>();
			localVars = new ArrayList<VariableDeclaration>();
			formalArgs = new ArrayList<VariableDeclaration>();
		}

		public String getTypeKey() {
			return classKey;
		}

		public void addField(VariableDeclaration node) {
			fields.add(node);
		}

		public void addLocalVar(VariableDeclaration node) {
			localVars.add(node);
		}

		public void addFormalArg(VariableDeclaration node) {
			formalArgs.add(node);
		}

		public List<VariableDeclaration> getFields() {
			return fields;
		}

		public List<VariableDeclaration> getLocalVars() {
			return localVars;
		}

		public List<VariableDeclaration> getFormalArgs() {
			return formalArgs;
		}

		public List<VariableDeclaration> getAll() {
			List<VariableDeclaration> allNodes 
						= new ArrayList<VariableDeclaration>(fields.size()
										+ localVars.size() + formalArgs.size());
			allNodes.addAll(fields);
			allNodes.addAll(localVars);
			allNodes.addAll(formalArgs);
			return allNodes;
		}

		public List<String> getFieldKeys() {
			List<String> keys = new ArrayList<String>(fields.size());
			addVarKeysToStringList(fields, keys);
			return keys;
		}

		public List<String> getLocalVarKeys() {
			List<String> keys = new ArrayList<String>(localVars.size());
			addVarKeysToStringList(localVars, keys);
			return keys;
		}

		public List<String> getFormalArgKeys() {
			List<String> keys = new ArrayList<String>(formalArgs.size());
			addVarKeysToStringList(formalArgs, keys);
			return keys;
		}

		public List<String> getAllVarKeys() {
			List<String> allVarKeys = new ArrayList<String>(fields.size()
										+ localVars.size() + formalArgs.size());

			addVarKeysToStringList(fields, allVarKeys);
			addVarKeysToStringList(localVars, allVarKeys);
			addVarKeysToStringList(formalArgs, allVarKeys);

			return allVarKeys;
		}

		private void addVarKeysToStringList(List<VariableDeclaration> nodes,
									 List<String> keys) {
			IVariableBinding vbind;
			for (VariableDeclaration node : nodes) {
				vbind = node.resolveBinding();
				if (vbind != null)
					keys.add(vbind.getKey());
			}
		}

		public boolean isEmpty() {
			return fields.isEmpty() && localVars.isEmpty() && formalArgs.isEmpty();
		}
	}
}