package com.tuxlogic.shiftiq.mobile.features.main.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.*
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate400
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate500
import com.tuxlogic.shiftiq.mobile.features.auth.infrastructure.local.UserDao
import com.tuxlogic.shiftiq.mobile.features.home.presentation.HomeScreen
import com.tuxlogic.shiftiq.mobile.features.inventory.presentation.InventoryScreen
import com.tuxlogic.shiftiq.mobile.features.main.presentation.screens.DiagnosticsScreen
import com.tuxlogic.shiftiq.mobile.features.main.presentation.screens.GarageScreen
import com.tuxlogic.shiftiq.mobile.features.main.presentation.screens.WorkshopsScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class MainTab(val title:String,val icon:ImageVector){HOME("Inicio",home),DIAGNOSTICS("Diagnóstico",warningAmber),WORKSHOPS("Talleres",build),GARAGE("Mi Garaje",directionsCar),INVENTORY("Inventario",build)}

@HiltViewModel
class MainScreenViewModel @Inject constructor(private val userDao:UserDao):ViewModel(){
 private val _canManageInventory=MutableStateFlow(false)
 val canManageInventory:StateFlow<Boolean> = _canManageInventory.asStateFlow()
 init{viewModelScope.launch{_canManageInventory.value=userDao.getActiveUser()?.role?.contains("OWNER",ignoreCase=true)==true}}
}

@Composable
fun MainScreen(modifier:Modifier=Modifier,viewModel:MainScreenViewModel=hiltViewModel()){
 val canManageInventory by viewModel.canManageInventory.collectAsState()
 var selected by rememberSaveable{mutableStateOf(MainTab.HOME)}
 val tabs=if(canManageInventory)MainTab.entries else MainTab.entries.filter{it!=MainTab.INVENTORY}
 Scaffold(modifier.fillMaxSize(),containerColor=Color.White,contentWindowInsets=WindowInsets.statusBars,bottomBar={
  NavigationBar(modifier=Modifier.navigationBarsPadding(),containerColor=Color.White,tonalElevation=8.dp){
   tabs.forEach{tab->val active=selected==tab
    NavigationBarItem(selected=active,onClick={selected=tab},icon={Icon(tab.icon,tab.title,Modifier.size(22.dp))},label={Text(tab.title,fontSize=11.sp,fontWeight=if(active)FontWeight.Bold else FontWeight.Medium)},colors=NavigationBarItemDefaults.colors(selectedIconColor=PrimaryBlue,selectedTextColor=PrimaryBlue,indicatorColor=Color(0xFFEFF6FF),unselectedIconColor=Slate400,unselectedTextColor=Slate500))
   }
  }
 }){padding->Box(Modifier.fillMaxSize().padding(padding)){Crossfade(selected,label="MainTabTransition"){tab->when(tab){
  MainTab.HOME->HomeScreen(onNavigateToDiagnostics={selected=MainTab.DIAGNOSTICS},onNavigateToWorkshops={selected=MainTab.WORKSHOPS})
  MainTab.DIAGNOSTICS->DiagnosticsScreen()
  MainTab.WORKSHOPS->WorkshopsScreen()
  MainTab.GARAGE->GarageScreen()
  MainTab.INVENTORY->InventoryScreen()
 }}}
 }
}
