package me.codvis.ast;

/**
 * Class for identifying model inside other model with multiple model type lists.
 */
public class ModelIdentifier{
	String modelType;
	int modelIndex;

	/**
	 * Constructs the object, setting tupe and index in list.
	 *
	 * @param      modelType   The model type
	 * @param      modelIndex  The model index
	 */
	public ModelIdentifier(String modelType, int modelIndex){
		this.modelType = modelType;
		this.modelIndex = modelIndex;
	}
}