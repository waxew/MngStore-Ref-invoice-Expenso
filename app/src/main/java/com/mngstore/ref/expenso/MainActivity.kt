package com.mngstore.ref.expenso

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.*
import java.text.NumberFormat
import java.util.Locale

/**
 * نمونه فارسی Expenso برای مدیریت و تحلیل هزینه‌ها.
 * تمرکز این نمونه روی دسته‌بندی هزینه و مقایسه درآمد با هزینه است.
 */
class MainActivity : Activity() {
    private lateinit var root: LinearLayout
    private val rows = mutableListOf<ExpenseRow>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;layoutDirection=View.LAYOUT_DIRECTION_RTL;setBackgroundColor(Color.rgb(248,248,250))}
        window.decorView.layoutDirection=View.LAYOUT_DIRECTION_RTL
        setContentView(root)
        // داده‌های نمونه فارسی برای تست فوری رابط.
        rows += ExpenseRow("هزینه","مواد اولیه",420_000)
        rows += ExpenseRow("هزینه","حمل‌ونقل",120_000)
        rows += ExpenseRow("درآمد","فروش",1_850_000)
        showHome()
    }

    /** داشبورد جمع درآمد، هزینه و مانده خالص. */
    private fun showHome(){
        root.removeAllViews(); header("تحلیل هزینه و درآمد","نمونه مرجع Expenso برای گالاتا")
        val income=rows.filter{it.type=="درآمد"}.sumOf{it.amount}; val expense=rows.filter{it.type=="هزینه"}.sumOf{it.amount}
        root.addView(card("درآمد کل: ${money(income)}\nهزینه کل: ${money(expense)}\nمانده خالص: ${money(income-expense)}"))
        root.addView(button("+ ثبت درآمد یا هزینه"){dialog()})
        root.addView(title("تحلیل دسته‌ها"))
        rows.filter{it.type=="هزینه"}.groupBy{it.category}.forEach{(cat,list)-> root.addView(card("$cat: ${money(list.sumOf{it.amount})}")) }
        root.addView(title("آخرین تراکنش‌ها"))
        rows.asReversed().forEach{root.addView(card("${it.type} — ${it.category}\n${money(it.amount)}"))}
    }

    /** فرم ثبت تراکنش همراه با دسته‌بندی. */
    private fun dialog(){
        val types=arrayOf("هزینه","درآمد"); val cats=arrayOf("مواد اولیه","حمل‌ونقل","اجاره","حقوق","فروش","سایر")
        val t=Spinner(this).apply{adapter=ArrayAdapter(this@MainActivity,android.R.layout.simple_spinner_dropdown_item,types)}
        val c=Spinner(this).apply{adapter=ArrayAdapter(this@MainActivity,android.R.layout.simple_spinner_dropdown_item,cats)}
        val a=EditText(this).apply{hint="مبلغ به تومان";inputType=InputType.TYPE_CLASS_NUMBER;gravity=Gravity.END}
        val form=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;layoutDirection=View.LAYOUT_DIRECTION_RTL;setPadding(dp(18),dp(8),dp(18),0);addView(t);addView(c);addView(a)}
        AlertDialog.Builder(this).setTitle("تراکنش جدید").setView(form).setNegativeButton("انصراف",null).setPositiveButton("ثبت"){_,_->
            val value=normalize(a.text.toString()).toLongOrNull()?:0L
            if(value<=0) toast("مبلغ معتبر وارد کنید") else {rows+=ExpenseRow(types[t.selectedItemPosition],cats[c.selectedItemPosition],value);showHome()}
        }.show()
    }

    private fun header(t:String,s:String){root.addView(LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(20),dp(20),dp(20),dp(16));setBackgroundColor(Color.rgb(91,72,140));addView(TextView(this@MainActivity).apply{text=t;textSize=24f;setTextColor(Color.WHITE);gravity=Gravity.END});addView(TextView(this@MainActivity).apply{text=s;textSize=13f;setTextColor(Color.LTGRAY);gravity=Gravity.END})})}
    private fun button(t:String,a:()->Unit)=Button(this).apply{text=t;isAllCaps=false;setOnClickListener{a()};layoutParams=LinearLayout.LayoutParams(-1,dp(54)).apply{setMargins(dp(16),dp(6),dp(16),dp(6))}}
    private fun title(t:String)=TextView(this).apply{text=t;textSize=18f;gravity=Gravity.END;setPadding(dp(18),dp(18),dp(18),dp(6))}
    private fun card(t:String)=TextView(this).apply{text=t;textSize=16f;gravity=Gravity.END;setPadding(dp(18),dp(16),dp(18),dp(16));setBackgroundColor(Color.WHITE);layoutParams=LinearLayout.LayoutParams(-1,-2).apply{setMargins(dp(16),dp(5),dp(16),dp(5))}}
    private fun money(v:Long)=fa(NumberFormat.getIntegerInstance(Locale.US).format(v).replace(",","٫"))+" تومان"
    private fun normalize(v:String)=v.replace('۰','0').replace('۱','1').replace('۲','2').replace('۳','3').replace('۴','4').replace('۵','5').replace('۶','6').replace('۷','7').replace('۸','8').replace('۹','9').replace("٫","").replace(",","").replace(" ","")
    private fun fa(v:String)=v.replace('0','۰').replace('1','۱').replace('2','۲').replace('3','۳').replace('4','۴').replace('5','۵').replace('6','۶').replace('7','۷').replace('8','۸').replace('9','۹')
    private fun toast(v:String)=Toast.makeText(this,v,Toast.LENGTH_SHORT).show()
    private fun dp(v:Int)=(v*resources.displayMetrics.density).toInt()
}

data class ExpenseRow(val type:String,val category:String,val amount:Long)
