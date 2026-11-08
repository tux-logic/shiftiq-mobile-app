package com.tuxlogic.shiftiq.mobile.features.inventory.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.warningAmber
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate200
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate500
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate900
import com.tuxlogic.shiftiq.mobile.features.inventory.domain.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(modifier:Modifier=Modifier,viewModel:InventoryViewModel=hiltViewModel()){
 val state by viewModel.uiState.collectAsState()
 var productDialog by remember{mutableStateOf(false)}
 var stockDialog by remember{mutableStateOf(false)}
 var editingId by remember{mutableStateOf<String?>(null)}
 Scaffold(modifier.fillMaxSize(),containerColor=Color(0xFFF8FAFC),contentWindowInsets=WindowInsets(0),
  topBar={TopAppBar(title={Column{Text("Inventario",fontWeight=FontWeight.Bold,color=Slate900);Text("Productos y existencias",fontSize=12.sp,color=Slate500)}},colors=TopAppBarDefaults.topAppBarColors(containerColor=Color.White))}){padding->
  Column(Modifier.fillMaxSize().padding(padding).padding(horizontal=16.dp)){
   if(state.isLoading&&state.branches.isEmpty()){CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally).padding(24.dp))}
   else if(state.branches.isEmpty()){InfoCard(if(state.isWorkshopUser)"Sin sucursales" else "Acceso de taller requerido",state.errorMessage?:if(state.isWorkshopUser)"Tu taller aún no tiene sucursales." else "Esta sección es para cuentas con perfil propietario de taller.")}
   else{
    BranchPicker(state.branches,state.selectedBranchId,viewModel::selectBranch)
    OutlinedTextField(state.searchQuery,viewModel::updateSearch,Modifier.fillMaxWidth(),label={Text("Buscar por nombre")},singleLine=true,shape=RoundedCornerShape(12.dp))
    Spacer(Modifier.height(6.dp))
    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(6.dp)){
     FilterChip(state.lowStockOnly,{viewModel.setLowStockOnly(!state.lowStockOnly)},label={Text("Stock bajo")},leadingIcon={Icon(warningAmber,null,Modifier.size(16.dp))})
     TextButton(onClick=viewModel::loadProducts){Text("Actualizar")}
     Spacer(Modifier.weight(1f))
     Button(onClick={editingId=null;productDialog=true}){Text("Nuevo")}
    }
    when{
     state.isLoading->CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally).padding(24.dp))
     state.products.isEmpty()->InfoCard("Inventario vacío","No hay productos para esta sucursal o búsqueda.")
     else->LazyColumn(verticalArrangement=Arrangement.spacedBy(10.dp),contentPadding=PaddingValues(vertical=10.dp)){items(state.products,key={it.id}){product->ProductCard(product,onOpen={viewModel.loadProduct(product.id)},onStock={editingId=product.id;stockDialog=true})}}
    }
   }
   state.errorMessage?.let{InfoCard("No se pudo completar",it)}
   state.infoMessage?.let{InfoCard("Listo",it)}
  }
 }
 if(productDialog){if(editingId!=null&&state.selectedProduct?.product?.id!=editingId){if(state.isProductLoading)CircularProgressIndicator(Modifier.padding(24.dp)) else viewModel.loadProduct(editingId!!)} else {val initial=state.selectedProduct?.product?.takeIf{it.id==editingId};ProductForm(initial,state.isSaving,{productDialog=false}){n,c,sku,d,price,min->viewModel.saveProduct(editingId,n,c,sku,d,price,min);productDialog=false}}}
 if(stockDialog){StockForm(state.isSaving,{stockDialog=false}){q,c->editingId?.let{viewModel.addStock(it,q,c)};stockDialog=false}}
 state.selectedProduct?.let{details->ProductDetails(details,onClose=viewModel::clearSelectedProduct,onEdit={editingId=details.product.id;productDialog=true},onStock={editingId=details.product.id;stockDialog=true},onDelete={viewModel.deleteProduct(details.product.id)})}
}
@Composable private fun BranchPicker(branches:List<InventoryBranch>,selectedId:String?,onSelect:(String)->Unit){
 var expanded by remember{mutableStateOf(false)};val selected=branches.firstOrNull{it.id==selectedId}
 Column(Modifier.padding(vertical=10.dp)){Text("Sucursal",fontSize=12.sp,color=Slate500,fontWeight=FontWeight.SemiBold);OutlinedButton(onClick={expanded=true},Modifier.fillMaxWidth()){Text(selected?.name?: "Selecciona una sucursal")};DropdownMenu(expanded,{expanded=false}){branches.forEach{DropdownMenuItem(text={Text(it.name)},onClick={expanded=false;onSelect(it.id)})}}}
}
@Composable private fun ProductCard(product:InventoryProduct,onOpen:()->Unit,onStock:()->Unit){
 Card(Modifier.fillMaxWidth().clickable(onClick=onOpen),shape=RoundedCornerShape(16.dp),colors=CardDefaults.cardColors(containerColor=Color.White),border=BorderStroke(1.dp,Slate200)){
  Column(Modifier.padding(14.dp)){
   Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Column(Modifier.weight(1f)){Text(product.name,color=Slate900,fontWeight=FontWeight.Bold);Text("${product.category} • SKU ${product.sku}",color=Slate500,fontSize=12.sp)};Text("S/ %.2f".format(product.salePrice),color=PrimaryBlue,fontWeight=FontWeight.Bold)}
   Spacer(Modifier.height(8.dp));Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)){Text("Stock ${product.currentStock} • mínimo ${product.minimumStock}",color=Slate900,fontSize=12.sp);if(product.lowStockAlert)AssistChip(onClick={},label={Text("Bajo")},leadingIcon={Icon(warningAmber,null,Modifier.size(14.dp))})}
   OutlinedButton(onClick=onStock){Text("Registrar lote / ajuste")}
  }
 }
}
@Composable private fun ProductDetails(details:InventoryProductDetails,onClose:()->Unit,onEdit:()->Unit,onStock:()->Unit,onDelete:()->Unit){
 var confirmDelete by remember{mutableStateOf(false)}
 AlertDialog(onDismissRequest=onClose,title={Text(details.product.name,fontWeight=FontWeight.Bold)},text={
  Column {Text("${details.product.category} • SKU ${details.product.sku}");Text("Stock ${details.product.currentStock} / mínimo ${details.product.minimumStock}");Text(details.product.description.orEmpty());Spacer(Modifier.height(12.dp));Text("Lotes registrados",fontWeight=FontWeight.Bold)
   if(details.batches.isEmpty())Text("No hay lotes asociados.",color=Slate500)
   details.batches.forEach{batch->HorizontalDivider(Modifier.padding(vertical=6.dp));Text("Inicial ${batch.initialQuantity} • disponible ${batch.availableQuantity}");Text("Costo S/ %.2f • %s".format(batch.acquisitionCost,batch.createdAt.orEmpty()),color=Slate500,fontSize=12.sp)}
  }
 },confirmButton={Button(onClick=onEdit){Text("Editar")}},dismissButton={Row(horizontalArrangement=Arrangement.spacedBy(4.dp)){TextButton(onClick=onStock){Text("Stock")};TextButton(onClick={confirmDelete=true}){Text("Eliminar")};TextButton(onClick=onClose){Text("Cerrar")}}})
 if(confirmDelete)AlertDialog(onDismissRequest={confirmDelete=false},title={Text("Eliminar producto")},text={Text("Se eliminará el producto y sus lotes.")},confirmButton={Button(onClick={confirmDelete=false;onDelete()},colors=ButtonDefaults.buttonColors(containerColor=Color(0xFFDC2626))){Text("Eliminar")}},dismissButton={TextButton(onClick={confirmDelete=false}){Text("Cancelar")}})
}
@Composable private fun ProductForm(initial:InventoryProduct?,saving:Boolean,onDismiss:()->Unit,onSave:(String,String,String,String,Double,Int)->Unit){
 var name by remember(initial?.id){mutableStateOf(initial?.name.orEmpty())};var category by remember(initial?.id){mutableStateOf(initial?.category.orEmpty())};var sku by remember(initial?.id){mutableStateOf(initial?.sku.orEmpty())};var description by remember(initial?.id){mutableStateOf(initial?.description.orEmpty())};var priceText by remember(initial?.id){mutableStateOf(initial?.salePrice?.toString().orEmpty())};var minText by remember(initial?.id){mutableStateOf(initial?.minimumStock?.toString().orEmpty())};var error by remember{mutableStateOf<String?>(null)}
 AlertDialog(onDismissRequest=onDismiss,title={Text(if(initial==null)"Nuevo producto" else "Editar producto")},text={Column(verticalArrangement=Arrangement.spacedBy(4.dp)){
  OutlinedTextField(name,{name=it},label={Text("Nombre")},singleLine=true);OutlinedTextField(category,{category=it},label={Text("Categoría")},singleLine=true);OutlinedTextField(sku,{sku=it},label={Text("SKU")},singleLine=true);OutlinedTextField(description,{description=it},label={Text("Descripción")});OutlinedTextField(priceText,{priceText=it},label={Text("Precio de venta (S/)")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal),singleLine=true);OutlinedTextField(minText,{minText=it},label={Text("Stock mínimo")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Number),singleLine=true);error?.let{Text(it,color=Color(0xFFB91C1C))}
 }},confirmButton={Button(enabled=!saving,onClick={val price=priceText.toDoubleOrNull();val minimum=minText.toIntOrNull();error=when{ name.isBlank()||category.isBlank()||sku.isBlank()->"Completa nombre, categoría y SKU.";price==null||price<=0->"El precio debe ser mayor que cero.";minimum==null||minimum<0->"El stock mínimo no puede ser negativo.";else->null};if(error==null)onSave(name.trim(),category.trim(),sku.trim(),description.trim(),price!!,minimum!!)} ){Text(if(saving)"Guardando…" else "Guardar")}},dismissButton={TextButton(onClick=onDismiss){Text("Cancelar")}})
}
@Composable private fun StockForm(saving:Boolean,onDismiss:()->Unit,onSave:(Int,Double)->Unit){
 var qty by remember{mutableStateOf("")};var cost by remember{mutableStateOf("")};var error by remember{mutableStateOf<String?>(null)}
 AlertDialog(onDismissRequest=onDismiss,title={Text("Registrar movimiento")},text={Column{Text("Usa positivo para ingreso y negativo para ajuste/salida. No puede ser cero.");OutlinedTextField(qty,{qty=it},label={Text("Cantidad (+ / −)")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Number),singleLine=true);OutlinedTextField(cost,{cost=it},label={Text("Costo de adquisición (S/)")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal),singleLine=true);error?.let{Text(it,color=Color(0xFFB91C1C))}}},confirmButton={Button(enabled=!saving,onClick={val q=qty.toIntOrNull();val c=cost.toDoubleOrNull();error=when{q==null||q==0->"Ingresa una cantidad distinta de cero.";c==null||c<0->"El costo no puede ser negativo.";else->null};if(error==null)onSave(q!!,c!!)} ){Text("Registrar")}},dismissButton={TextButton(onClick=onDismiss){Text("Cancelar")}})
}
@Composable private fun InfoCard(title:String,message:String){Card(Modifier.fillMaxWidth().padding(vertical=8.dp),colors=CardDefaults.cardColors(containerColor=Color.White),border=BorderStroke(1.dp,Slate200)){Column(Modifier.padding(16.dp)){Text(title,fontWeight=FontWeight.Bold,color=Slate900);Spacer(Modifier.height(4.dp));Text(message,color=Slate500,fontSize=13.sp)}}}
