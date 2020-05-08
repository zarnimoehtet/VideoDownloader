package com.abc.videodownloader


import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abc.videodownloader.adapter.Adapter_VideoFolder
import com.abc.videodownloader.model.Model_Video
import com.abc.videodownloader.utils.Constants
import com.abc.videodownloader.utils.Utils

/**
 * A simple [Fragment] subclass.
 */
class FrontFragment : Fragment() {

    var obj_adapter: Adapter_VideoFolder? = null
    var al_video = ArrayList<Model_Video>()
    public var recyclerView1: RecyclerView? = null
    var recyclerViewLayoutManager: RecyclerView.LayoutManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
    var  v: View = inflater.inflate(R.layout.fragment_front, container, false)
        recyclerView1 = v.findViewById(R.id.recycler)

        val title: TextView = v.findViewById(R.id.title)
        title.setTypeface(Utils.fontsetter.getMMTypeface(context!!))


        recyclerViewLayoutManager = GridLayoutManager(context!!, 3) as RecyclerView.LayoutManager?
        recyclerView1!!.setLayoutManager(recyclerViewLayoutManager)
        fn_video(context!!, true)
        return v
    }

    fun fn_video(cn: Context, f: Boolean) {
        al_video = ArrayList<Model_Video>()
        val int_position = 0
        val uri: Uri
        val cursor: Cursor
        val column_index_data: Int
        val column_index_folder_name: Int
        val column_id: Int
        val thum: Int
        val duration: Int

        var absolutePathOfImage: String? = null
        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val condition = MediaStore.Video.Media.DATA + " like?"
        val selectionArguments = arrayOf("%${Constants.DOWNLOAD_DIRECTORY}%")
        val sortOrder = MediaStore.Video.Media.DATE_TAKEN + " DESC"
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Video.Media.DURATION
        )
        cursor = cn!!.contentResolver.query(
            uri,
            projection,
            condition,
            selectionArguments,
            "$sortOrder"
        )!!

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        column_index_folder_name =
            cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
        column_id = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
        thum = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA)
        duration = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
        var i: Int = 0
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data)
            Log.e("Column", absolutePathOfImage)
            Log.e("Folder", cursor.getString(column_index_folder_name))
            Log.e("column_id", cursor.getString(column_id))
            Log.e("thum", cursor.getString(thum))
            Log.e("duration", cursor.getString(duration))

            val obj_model = Model_Video()
            obj_model.isBoolean_selected = false
            obj_model.str_path = absolutePathOfImage
            obj_model.str_thumb = cursor.getString(thum)
            obj_model.duration = cursor.getInt(duration)
            obj_model.id = i

            al_video.add(obj_model)
            i = i + 1
        }


        obj_adapter = Adapter_VideoFolder(cn!!, al_video)
        recyclerView1!!.setAdapter(obj_adapter)
        obj_adapter!!.notifyDataSetChanged()
        print("ITEM COUNT==>" + i)

//
//        //recyclerView1!!.setLayoutManager(null);
//        recyclerView1!!.getRecycledViewPool().clear();
//        recyclerView1!!.swapAdapter(obj_adapter, false);
//       // recyclerView1!!.setLayoutManager(layoutManager);
//        obj_adapter!!.notifyDataSetChanged();


    }



}
