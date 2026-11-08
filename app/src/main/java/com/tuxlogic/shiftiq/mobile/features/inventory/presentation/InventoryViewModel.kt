package com.tuxlogic.shiftiq.mobile.features.inventory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.features.inventory.domain.InventoryRepository
import com.tuxlogic.shiftiq.mobile.features.inventory.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(private val repository:InventoryRepository):ViewModel(){
 private val _uiState=MutableStateFlow(InventoryUiState())
 val uiState:StateFlow<InventoryUiState> = _uiState.asStateFlow()
 private var searchJob:Job?=null
 init{loadBranches()}
 fun loadBranches(){viewModelScope.launch{
  _uiState.update{it.copy(isLoading=true,errorMessage=null)}
  repository.getAuthorizedBranches().fold({pairs->
   val branches=pairs.map{it.second};val workshops=pairs.map{it.first}.distinctBy{it.id}
   val selected=_uiState.value.selectedBranchId?.takeIf{id->branches.any{it.id==id}}?:branches.firstOrNull()?.id
   _uiState.update{it.copy(isLoading=false,isWorkshopUser=true,workshops=workshops,branches=branches,selectedBranchId=selected)}
   if(selected!=null)loadProducts()
  },{e->_uiState.update{it.copy(isLoading=false,isWorkshopUser=false,errorMessage=e.message?: "No se pudieron cargar las sucursales.")}})
 }}
 fun selectBranch(id:String){_uiState.update{it.copy(selectedBranchId=id,selectedProduct=null)};loadProducts()}
 fun updateSearch(value:String){_uiState.update{it.copy(searchQuery=value)};searchJob?.cancel();searchJob=viewModelScope.launch{delay(350);loadProducts()}}
 fun setLowStockOnly(value:Boolean){_uiState.update{it.copy(lowStockOnly=value)};loadProducts()}
 fun loadProducts(){val s=_uiState.value;val branch=s.selectedBranchId?:return;viewModelScope.launch{
  _uiState.update{it.copy(isLoading=true,errorMessage=null)}
  repository.getProducts(branch,s.searchQuery,s.categoryFilter,s.lowStockOnly).fold({items->_uiState.update{it.copy(isLoading=false,products=items)}},{e->_uiState.update{it.copy(isLoading=false,errorMessage=e.message?: "No se pudo cargar el inventario.")}})
 }}
 fun loadProduct(id:String){viewModelScope.launch{
  _uiState.update{it.copy(isProductLoading=true,errorMessage=null)}
  repository.getProduct(id).fold({detail->_uiState.update{it.copy(isProductLoading=false,selectedProduct=detail)}},{e->_uiState.update{it.copy(isProductLoading=false,errorMessage=e.message?: "No se pudo cargar el producto.")}})
 }}
 fun clearSelectedProduct(){_uiState.update{it.copy(selectedProduct=null)}}
 fun saveProduct(id:String?,name:String,category:String,sku:String,description:String,price:Double,min:Int){val branch=_uiState.value.selectedBranchId?:return;viewModelScope.launch{
  _uiState.update{it.copy(isSaving=true,errorMessage=null,infoMessage=null)}
  val result=if(id==null)repository.createProduct(branch,name,category,sku,description,price,min) else repository.updateProduct(id,name,category,sku,description,price,min)
  result.fold({_-> _uiState.update{it.copy(isSaving=false,infoMessage=if(id==null)"Producto creado." else "Producto actualizado.")};loadProducts();if(id!=null)loadProduct(id)},{e->_uiState.update{it.copy(isSaving=false,errorMessage=e.message?: "No se pudo guardar el producto.")}})
 }}
 fun addStock(id:String,quantity:Int,cost:Double){viewModelScope.launch{_uiState.update{it.copy(isSaving=true,errorMessage=null,infoMessage=null)};repository.addStock(id,quantity,cost).fold({_-> _uiState.update{it.copy(isSaving=false,infoMessage="Movimiento de stock registrado.")};loadProducts();loadProduct(id)},{e->_uiState.update{it.copy(isSaving=false,errorMessage=e.message?: "No se pudo registrar el movimiento.")}})}}
 fun deleteProduct(id:String){viewModelScope.launch{_uiState.update{it.copy(isSaving=true,errorMessage=null)};repository.deleteProduct(id).fold({_-> _uiState.update{it.copy(isSaving=false,selectedProduct=null,infoMessage="Producto eliminado.")};loadProducts()},{e->_uiState.update{it.copy(isSaving=false,errorMessage=e.message?: "No se pudo eliminar el producto.")}})}}
}